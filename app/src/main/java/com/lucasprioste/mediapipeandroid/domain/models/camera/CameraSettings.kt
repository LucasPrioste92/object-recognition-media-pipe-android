package com.lucasprioste.mediapipeandroid.domain.models.camera

import androidx.camera.core.CameraSelector

data class CameraSettings(
    val currentCamera: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
)
