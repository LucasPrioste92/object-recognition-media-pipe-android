package com.lucasprioste.mediapipeandroid.presentation.object_detection_screen.components

import android.graphics.Matrix
import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetectorResult
import com.lucasprioste.mediapipeandroid.R
import kotlin.math.max

@Composable
fun ObjectDetectorOverlay(
    modifier: Modifier = Modifier,
    results: List<ObjectDetectorResult>? = null,
    outputWidth: Int = 0,
    outputHeight: Int = 0,
    imageRotation: Int = 0,
    clear: Boolean = false
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val textSize = with(density) { 16.sp.toPx() }
    val strokeWidth = with(density) { 8.dp.toPx() }
    val textPadding = with(density) { 8.dp.toPx() }

    // Create primary color from resources
    val primaryColor = remember {
        Color(ContextCompat.getColor(context, R.color.purple_200))
    }

    // Create text paint for rendering labels
    val textPaint = remember {
        android.graphics.Paint().apply {
            color = Color.White.toArgb()
            this.textSize = textSize
        }
    }

    // Create background paint for text labels
    val backgroundPaint = remember {
        Paint().apply {
            color = Color.Black
            style = PaintingStyle.Fill
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        if (results == null || outputWidth <= 0 || outputHeight <= 0 || clear) {
            return@Canvas
        }

        // Calculate the rotated width and height
        val rotatedWidthHeight = when (imageRotation) {
            0, 180 -> Pair(outputWidth, outputHeight)
            90, 270 -> Pair(outputHeight, outputWidth)
            else -> return@Canvas
        }

        // Calculate scale factor based on running mode
        val scaleFactor = max(
            size.width / rotatedWidthHeight.first,
            size.height / rotatedWidthHeight.second,
        )

        val translateX = (size.width - rotatedWidthHeight.first * scaleFactor) / 2f
        val translateY = (size.height - rotatedWidthHeight.second * scaleFactor) / 2f

        // Process and draw all detections
        results.forEach { result ->
            result.detections().forEachIndexed { index, detection ->

                // Get the original bounding box
                val boxRect = RectF(
                    detection.boundingBox().left,
                    detection.boundingBox().top,
                    detection.boundingBox().right,
                    detection.boundingBox().bottom
                )

                // Create and apply matrix for rotation handling
                val matrix = Matrix()
                matrix.postTranslate(-outputWidth / 2f, -outputHeight / 2f)

                // Rotate box
                matrix.postRotate(imageRotation.toFloat())

                // Handle translation after rotation for 90/270 degrees
                if (imageRotation == 90 || imageRotation == 270) {
                    matrix.postTranslate(outputHeight / 2f, outputWidth / 2f)
                } else {
                    matrix.postTranslate(outputWidth / 2f, outputHeight / 2f)
                }

                // Apply transformations to the bounding box
                matrix.mapRect(boxRect)

                // Apply scale factor and translation
                val left = boxRect.left * scaleFactor + translateX
                val top = boxRect.top * scaleFactor + translateY
                val right = boxRect.right * scaleFactor + translateX
                val bottom = boxRect.bottom * scaleFactor + translateY

                // Draw bounding box around detected objects
                drawRect(
                    color = primaryColor,
                    topLeft = Offset(left, top),
                    size = Size(right - left, bottom - top),
                    style = Stroke(width = strokeWidth)
                )

                // Create text to display alongside detected objects
                val category = detection.categories()[0]
                val drawableText =
                    "${category.categoryName()} ${String.format("%.2f", category.score())}"

                // Measure text to calculate background rectangle size
                val textBounds = android.graphics.Rect()
                val tempPaint = android.graphics.Paint().apply {
                    this.textSize = textPaint.textSize
                }
                tempPaint.getTextBounds(drawableText, 0, drawableText.length, textBounds)
                val textWidth = textBounds.width()
                val textHeight = textBounds.height()

                // Draw rect behind display text
                drawIntoCanvas { canvas ->
                    canvas.nativeCanvas.drawRect(
                        left,
                        top,
                        left + textWidth + textPadding,
                        top + textHeight + textPadding,
                        backgroundPaint.asFrameworkPaint()
                    )

                    // Draw text for detected object
                    canvas.nativeCanvas.drawText(
                        drawableText,
                        left,
                        top + textHeight,
                        textPaint
                    )
                }
            }
        }
    }
}