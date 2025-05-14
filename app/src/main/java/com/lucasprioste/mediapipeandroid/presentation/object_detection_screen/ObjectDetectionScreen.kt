package com.lucasprioste.mediapipeandroid.presentation.object_detection_screen

import android.Manifest
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.lucasprioste.mediapipeandroid.domain.models.camera.CameraSettings
import com.lucasprioste.mediapipeandroid.domain.models.object_detection.ObjectDetectionResult
import com.lucasprioste.mediapipeandroid.presentation.object_detection_screen.components.CameraControls
import com.lucasprioste.mediapipeandroid.presentation.object_detection_screen.components.CameraPreview
import com.lucasprioste.mediapipeandroid.presentation.object_detection_screen.components.ObjectDetectorOverlay

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ObjectDetectionScreen(
    objectDetectionResult: ObjectDetectionResult,
    cameraSettings: CameraSettings,
    onEvent: (ObjectDetectionEvent) -> Unit,
) {
    val context = LocalContext.current
    val permissionState = rememberPermissionState(Manifest.permission.CAMERA)
    RequestCameraPermission(state = permissionState)

    val controller = remember {
        LifecycleCameraController(context).apply {
            cameraControl.apply {
                setLinearZoom(0f)
                cameraSelector = cameraSettings.currentCamera
            }
            setImageAnalysisAnalyzer(
                ContextCompat.getMainExecutor(context),
            ) { image ->
                onEvent(ObjectDetectionEvent.OnNewImageFrame(image = image))
            }
        }
    }

    LaunchedEffect(cameraSettings) {
        controller.cameraSelector = cameraSettings.currentCamera
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (permissionState.status.isGranted) {
            CameraPreview(
                modifier = Modifier.fillMaxSize(),
                controller = controller,
            )
        }
        ObjectDetectorOverlay(
            modifier = Modifier.fillMaxSize(),
            results = objectDetectionResult.results,
            imageRotation = objectDetectionResult.outputImageRotation,
            outputWidth = objectDetectionResult.outputImageWidth,
            outputHeight = objectDetectionResult.outputImageHeight,
        )
        Text(
            text = "Object Detection",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = WindowInsets.safeContent.asPaddingValues().calculateTopPadding()),
            textAlign = TextAlign.Center,
        )
        CameraControls(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(bottom = 24.dp),
            onSwitchCameraSelector = { onEvent(ObjectDetectionEvent.OnCameraSwitch) }
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestCameraPermission(
    state: PermissionState,
) {
    LaunchedEffect(state.status) {
        if (!state.status.isGranted) state.launchPermissionRequest()
    }
}