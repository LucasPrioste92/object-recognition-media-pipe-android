package com.lucasprioste.mediapipeandroid.data.repository

import androidx.camera.core.ImageProxy
import com.lucasprioste.mediapipeandroid.common.ObjectDetectorHelper

class ObjectDetectionRepository(
    private val objectDetectorHelper: ObjectDetectorHelper,
) {
    fun sendImageForDetection(image: ImageProxy) {
        objectDetectorHelper.classifyObject(image)
    }

    fun observeLiveDetectionResult() = objectDetectorHelper.getLiveResults()
}