package com.lucasprioste.mediapipeandroid.presentation.object_detection_screen

import androidx.camera.core.ImageProxy

sealed class ObjectDetectionEvent {
    data class OnNewImageFrame(val image: ImageProxy): ObjectDetectionEvent()
    data object OnCameraSwitch: ObjectDetectionEvent()
}
