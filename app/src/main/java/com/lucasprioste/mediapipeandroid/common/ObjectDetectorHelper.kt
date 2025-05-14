package com.lucasprioste.mediapipeandroid.common

import android.content.Context
import android.os.SystemClock
import android.util.Log
import androidx.camera.core.ImageProxy
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.ImageProcessingOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetector
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetectorResult
import com.lucasprioste.mediapipeandroid.domain.models.object_detection.ObjectDetectionModel
import com.lucasprioste.mediapipeandroid.domain.models.object_detection.ObjectDetectionResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class ObjectDetectorHelper(
    private val context: Context,
    private var threshold: Float = 0.3f,
    private var maxResults: Int = 5,
    private var currentDelegate: Delegate = Delegate.CPU,
    private var currentModel: ObjectDetectionModel = ObjectDetectionModel.EFFICIENTDET,
    private var runningMode: RunningMode = RunningMode.LIVE_STREAM,
) {
    private var objectDetector: ObjectDetector? = null
    private var imageRotation = 0
    private val detectorScope = CoroutineScope(Dispatchers.IO)

    private val results = MutableSharedFlow<ObjectDetectionResult>(replay = 0)

    init {
        setupObjectDetector()
    }

    // Initialize the object detector using current settings on the
    // thread that is using it. CPU can be used with detectors
    // that are created on the main thread and used on a background thread, but
    // the GPU delegate needs to be used on the thread that initialized the detector
    private fun setupObjectDetector() {
        val baseOptionsBuilder = BaseOptions.builder()
            .setDelegate(currentDelegate)
            .setModelAssetPath(currentModel.fileName)

        val options = ObjectDetector.ObjectDetectorOptions.builder()
            .setBaseOptions(baseOptionsBuilder.build())
            .setScoreThreshold(threshold)
            .setRunningMode(runningMode)
            .setMaxResults(maxResults)
            .setResultListener(this::returnLivestreamResult)
            .build()

        try {
            objectDetector = ObjectDetector.createFromOptions(context, options)
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            Log.e(TAG, "TFLite failed to load model with error: " + e.message)
        } catch (e: RuntimeException) {
            e.printStackTrace()
            Log.e(TAG, "Object detector failed to load model with error: " + e.message)
        }
    }

    // Return the detection result to this ObjectDetectorHelper's caller
    private fun returnLivestreamResult(
        result: ObjectDetectorResult,
        input: MPImage,
    ) {
        val finishTimeMs = SystemClock.uptimeMillis()
        val inferenceTime = finishTimeMs - result.timestampMs()

        detectorScope.launch {
            results.emit(
                ObjectDetectionResult(
                    results = listOf(result),
                    inferenceTime = inferenceTime,
                    outputImageHeight = input.height,
                    outputImageWidth = input.width,
                    outputImageRotation = imageRotation,
                )
            )
        }
    }

    // Analyze image frames from camera
    fun classifyObject(imageProxy: ImageProxy) {
        if (objectDetector == null) setupObjectDetector()

        val frameTime = SystemClock.uptimeMillis()

        val bitmapBuffer = imageProxy.toBitmap()
        imageProxy.close()

        val imageProcessingOptions = ImageProcessingOptions.builder()
            .setRotationDegrees(imageRotation)
            .build()

        if (imageProxy.imageInfo.rotationDegrees != imageRotation) {
            imageRotation = imageProxy.imageInfo.rotationDegrees
        }

        val mpImage = BitmapImageBuilder(bitmapBuffer).build()
        objectDetector?.detectAsync(mpImage, imageProcessingOptions, frameTime)
        mpImage.close()
    }

    fun getLiveResults() = flow {
        results.collect { result -> emit(result) }
    }

    companion object {
        const val TAG = "ObjectDetectorHelper"
    }
}