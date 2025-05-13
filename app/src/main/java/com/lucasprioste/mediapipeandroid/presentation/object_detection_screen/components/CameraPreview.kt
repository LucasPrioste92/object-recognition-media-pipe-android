package com.lucasprioste.mediapipeandroid.presentation.object_detection_screen.components

import android.annotation.SuppressLint
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.lucasprioste.mediapipeandroid.domain.models.object_detection.ObjectDetectionResultBundle

@SuppressLint("RestrictedApi")
@Composable
fun CameraPreview(
    controller: LifecycleCameraController,
    onResults: (ObjectDetectionResultBundle) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    DisposableEffect(Unit) {
        onDispose {
            cameraProviderFuture.get().unbindAll()
        }
    }

    AndroidView(
        factory = {
            PreviewView(it).apply {
                this.controller = controller
                controller.bindToLifecycle(lifecycleOwner)
            }
            /*val previewView = PreviewView(context)
            val executor = ContextCompat.getMainExecutor(context)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                // We set a surface for the camera input feed to be displayed in, which is
                // in the camera preview view we just instantiated
                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                }

                // We specify what phone camera to use. In our case it's the back camera
                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build()

                // We instantiate an image analyser to apply some transformations on the
                // input frame before feeding it to the object detector
                val imageAnalyzer =
                    ImageAnalysis.Builder()
                        .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                        .build()

                val backgroundExecutor = Executors.newSingleThreadExecutor()

                backgroundExecutor.execute {
                    val objectDetectorHelper = ObjectDetectorHelper(
                        context = context,
                        onResults = onResults,
                    )

                    imageAnalyzer.setAnalyzer(
                        backgroundExecutor,
                        objectDetectorHelper::classifyObject
                    )
                }

                // We close any currently open camera just in case, then open up
                // our own to be display the live camera feed
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    imageAnalyzer,
                    preview
                )
            }, executor)
            previewView*/
        },
        modifier = modifier,
    )
}