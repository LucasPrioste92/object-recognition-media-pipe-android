package com.lucasprioste.mediapipeandroid.domain.models.object_detection

import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetectorResult

data class ObjectDetectionResult(
    val results: List<ObjectDetectorResult> = emptyList(),
    val inferenceTime: Long = 0L,
    val outputImageHeight: Int = 0,
    val outputImageWidth: Int = 0,
    val outputImageRotation: Int = 0
)
