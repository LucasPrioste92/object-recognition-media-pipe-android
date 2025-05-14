package com.lucasprioste.mediapipeandroid.presentation.object_detection_screen

import com.lucasprioste.mediapipeandroid.domain.models.camera.CameraSettings
import com.lucasprioste.mediapipeandroid.domain.models.object_detection.ObjectDetectionResult

data class ObjectDetectionUIState(
    val isLoading: Boolean = false,
    val objectDetectionResult: ObjectDetectionResult = ObjectDetectionResult(),
    val cameraSettings: CameraSettings = CameraSettings(),
)