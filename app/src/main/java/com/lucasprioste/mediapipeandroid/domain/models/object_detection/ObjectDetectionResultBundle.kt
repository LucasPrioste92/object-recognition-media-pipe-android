package com.lucasprioste.mediapipeandroid.domain.models.object_detection

import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetectorResult

data class ObjectDetectionResultBundle(
    val results: List<ObjectDetectorResult>,
    val inferenceTime: Long,
    val inputImageHeight: Int,
    val inputImageWidth: Int,
    val inputImageRotation: Int = 0
)
