package com.lucasprioste.mediapipeandroid.domain.models.object_detection

sealed class ObjectDetectionModel(
    val fileName: String,
    val expectedWidth: Int,
    val expectedHeight: Int,
) {
    data object EFFICIENTDET: ObjectDetectionModel(
        fileName = "object_detection.tflite",
        expectedWidth = 640,
        expectedHeight = 480,
    )
}