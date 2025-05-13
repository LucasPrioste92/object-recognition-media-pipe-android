package com.lucasprioste.mediapipeandroid.presentation.object_detection_screen.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetectorResult
import com.lucasprioste.mediapipeandroid.presentation.core.utils.getSolidColorForName
import com.lucasprioste.mediapipeandroid.presentation.core.utils.getWidthAndHeightBasedOnRotation
import com.lucasprioste.mediapipeandroid.presentation.core.utils.rotationHandling
import kotlin.math.max

@Composable
fun ObjectDetectorOverlay(
    modifier: Modifier = Modifier,
    results: List<ObjectDetectorResult>? = null,
    outputWidth: Int = 0,
    outputHeight: Int = 0,
    imageRotation: Int = 0,
) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val strokeWidth = with(density) { 4.dp.toPx() }
    val textPadding = with(density) { 8.dp.toPx() }

    Canvas(modifier = modifier.fillMaxSize()) {
        if (results == null || outputWidth <= 0 || outputHeight <= 0) return@Canvas

        val rotatedWidthHeight = imageRotation.getWidthAndHeightBasedOnRotation(
            width = outputWidth,
            height = outputHeight,
        )

        val scaleFactor = max(
            a = size.width / rotatedWidthHeight.first,
            b = size.height / rotatedWidthHeight.second,
        )

        val translateX = (size.width - rotatedWidthHeight.first * scaleFactor) / 2f
        val translateY = (size.height - rotatedWidthHeight.second * scaleFactor) / 2f

        results.forEach { result ->
            result.detections().forEach { detection ->
                val boxRect = detection.boundingBox()
                // Create and apply matrix for rotation handling
                boxRect.rotationHandling(
                    rotation = imageRotation,
                    width = outputWidth,
                    height = outputHeight,
                )

                // Apply scale factor and translation
                val left = boxRect.left * scaleFactor + translateX
                val top = boxRect.top * scaleFactor + translateY
                val right = boxRect.right * scaleFactor + translateX
                val bottom = boxRect.bottom * scaleFactor + translateY

                // Create text to display alongside detected objects
                val categoryName = detection.categories().first().categoryName()
                val boxColor = getSolidColorForName(categoryName)

                // Draw bounding box around detected objects
                drawObjectRect(
                    color = boxColor,
                    topLeft = Offset(left, top),
                    size = Size(right - left, bottom - top),
                    style = Stroke(width = strokeWidth),
                )

                drawObjectInfo(
                    color = boxColor,
                    topLeft = Offset(left, top),
                    textMeasurer = textMeasurer,
                    categoryName = categoryName,
                    textPadding = textPadding,
                )
            }
        }
    }
}

private fun DrawScope.drawObjectRect(
    color: Color,
    topLeft: Offset,
    size: Size,
    style: Stroke,
) {
    drawRect(
        color = color,
        topLeft = topLeft,
        size = size,
        style = style
    )
}

private fun DrawScope.drawObjectInfo(
    color: Color,
    categoryName: String,
    topLeft: Offset,
    textMeasurer: TextMeasurer,
    textPadding: Float,
) {
    val textLayoutResult = textMeasurer.measure(
        text = AnnotatedString(categoryName.replaceFirstChar { it.uppercase() }),
        style = TextStyle(color = Color.Black, fontSize = 14.sp)
    )
    val textWidth = textLayoutResult.size.width
    val textHeight = textLayoutResult.size.height
    val backgroundWidth = textWidth + textPadding
    val backgroundHeight = textHeight + textPadding

    drawRect(
        color = color,
        topLeft = topLeft,
        size = Size(backgroundWidth, backgroundHeight),
        style = Fill,
    )
    drawText(
        textLayoutResult = textLayoutResult,
        topLeft = Offset(
            x = topLeft.x,
            y = topLeft.y + textHeight - textLayoutResult.firstBaseline
        ),
    )
}