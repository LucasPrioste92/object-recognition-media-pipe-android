package com.lucasprioste.mediapipeandroid.presentation.object_detection_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.lucasprioste.mediapipeandroid.R

@Composable
fun CameraControls(
    modifier: Modifier,
    onSwitchCameraSelector: () -> Unit,
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(color = Color.Transparent)
            .border(width = 2.dp, color = Color.Black, shape = CircleShape)
            .clickable(onClick = onSwitchCameraSelector),
    ) {
        Icon(
            modifier = Modifier
                .size(60.dp)
                .padding(10.dp),
            painter = painterResource(R.drawable.ic_flip_camera),
            contentDescription = null,
        )
    }
}