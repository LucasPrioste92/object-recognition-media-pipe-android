package com.lucasprioste.mediapipeandroid.presentation.object_detection_screen

import android.Manifest
import android.util.Log
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.lucasprioste.mediapipeandroid.common.ObjectDetectorHelper
import com.lucasprioste.mediapipeandroid.domain.models.object_detection.ObjectDetectionResultBundle
import com.lucasprioste.mediapipeandroid.presentation.object_detection_screen.components.CameraPreview
import com.lucasprioste.mediapipeandroid.presentation.object_detection_screen.components.ObjectDetectorOverlay

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ObjectDetectionScreen() {
    val context = LocalContext.current
    val state = rememberPermissionState(Manifest.permission.CAMERA)
    RequestCameraPermission(state = state)

    var results by remember { mutableStateOf<ObjectDetectionResultBundle?>(null) }

    val analyzer = remember {
        ObjectDetectorHelper(
            context = context,
            onResults = {
                Log.i("TEST", "Results: $it")
                results = it
            },
        )
    }

    val controller = remember {
        LifecycleCameraController(context).apply {
            setImageAnalysisAnalyzer(
                ContextCompat.getMainExecutor(context),
                analyzer,
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (state.status.isGranted) {
            CameraPreview(
                modifier = Modifier.fillMaxSize(),
                controller = controller,
            )
        }
        ObjectDetectorOverlay(
            modifier = Modifier.fillMaxSize(),
            results = results?.results,
            imageRotation = results?.inputImageRotation ?: 0,
            outputWidth = results?.inputImageWidth ?: 0,
            outputHeight = results?.inputImageHeight ?: 0,
        )
        Text(
            text = "Object Detection",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = WindowInsets.safeContent.asPaddingValues().calculateTopPadding()),
            textAlign = TextAlign.Center,
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