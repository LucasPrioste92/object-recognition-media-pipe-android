package com.lucasprioste.mediapipeandroid.presentation.object_detection_screen

import androidx.camera.core.CameraSelector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lucasprioste.mediapipeandroid.data.repository.ObjectDetectionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ObjectDetectionViewModel(
    private val objectDetectionRepository: ObjectDetectionRepository,
): ViewModel() {
    private val _uiState = MutableStateFlow(ObjectDetectionUIState())
    val uiState = _uiState.asStateFlow()

    init {
        observeLiveDetectionResult()
    }

    fun onEvent(event: ObjectDetectionEvent) {
        when (event) {
            is ObjectDetectionEvent.OnNewImageFrame -> objectDetectionRepository.sendImageForDetection(
                image = event.image,
            )

            ObjectDetectionEvent.OnCameraSwitch -> switchCamera()
        }
    }

    private fun observeLiveDetectionResult() {
        viewModelScope.launch(Dispatchers.IO) {
            objectDetectionRepository.observeLiveDetectionResult().collect { result ->
                _uiState.update { it.copy(objectDetectionResult = result) }
            }
        }
    }

    private fun switchCamera() {
        _uiState.update {
            it.copy(
                cameraSettings = it.cameraSettings.copy(
                    currentCamera = if (it.cameraSettings.currentCamera == CameraSelector.DEFAULT_BACK_CAMERA)
                        CameraSelector.DEFAULT_FRONT_CAMERA
                    else CameraSelector.DEFAULT_BACK_CAMERA,
                )
            )
        }
    }
}