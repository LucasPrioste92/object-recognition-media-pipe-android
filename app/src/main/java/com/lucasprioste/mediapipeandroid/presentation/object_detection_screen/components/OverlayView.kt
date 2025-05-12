package com.lucasprioste.mediapipeandroid.presentation.object_detection_screen.components

import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import android.graphics.Paint
import android.graphics.RectF
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetectorResult
import com.lucasprioste.mediapipeandroid.R
import java.util.Locale

@Composable
fun ObjectDetectorOverlay(
    modifier: Modifier = Modifier,
    detectionResults: List<ObjectDetectorResult>? = null,
    componentWidth: Int = 0,
    componentHeight: Int = 0,
    imageRotation: Int? = 0,
    imageAnalysedWidth: Int = 0,
    imageAnalysedHeight: Int = 0,
) {
    val context = LocalContext.current

    val textPaint = remember {
        Paint().apply {
            color = WHITE
            textSize = 50f
            style = Paint.Style.FILL
        }
    }

    val textBackgroundPaint = remember {
        Paint().apply {
            color = BLACK
            style = Paint.Style.FILL
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (detectionResults == null) return@Canvas

            detectionResults.forEach { result ->
                result.detections().forEach { detection ->
                    // Get the bounding box from the detection
                    val bbox = detection.boundingBox()

                    val rotatedBox = when (imageRotation) {
                        0 -> RectF(
                            bbox.left / imageAnalysedWidth * componentWidth,
                            bbox.top / imageAnalysedHeight * componentHeight,
                            bbox.right / imageAnalysedWidth * componentWidth,
                            bbox.bottom / imageAnalysedHeight * componentHeight
                        )

                        90 -> RectF(
                            (imageAnalysedHeight - bbox.bottom) / imageAnalysedHeight * componentWidth,
                            bbox.left / imageAnalysedWidth * componentHeight,
                            (imageAnalysedHeight - bbox.top) / imageAnalysedHeight * componentWidth,
                            bbox.right / imageAnalysedWidth * componentHeight
                        )

                        180 -> RectF(
                            (imageAnalysedWidth - bbox.right) / imageAnalysedWidth * componentWidth,
                            (imageAnalysedHeight - bbox.bottom) / imageAnalysedHeight * componentHeight,
                            (imageAnalysedWidth - bbox.left) / imageAnalysedWidth * componentWidth,
                            (imageAnalysedHeight - bbox.top) / imageAnalysedHeight * componentHeight
                        )

                        270 -> RectF(
                            bbox.top / imageAnalysedHeight * componentWidth,
                            (imageAnalysedWidth - bbox.right) / imageAnalysedWidth * componentHeight,
                            bbox.bottom / imageAnalysedHeight * componentWidth,
                            (imageAnalysedWidth - bbox.left) / imageAnalysedWidth * componentHeight
                        )

                        else -> throw IllegalArgumentException("Unsupported rotation: $imageRotation")
                    }

                    // Debug log
                    Log.d(
                        "ObjectDetectorOverlay",
                        "View: ${componentWidth}x${componentHeight}, " +
                                "Orig: $bbox, Rot: $rotatedBox, RotDeg: $imageRotation"
                    )

                    // Draw bounding box
                    drawRect(
                        color = Color(ContextCompat.getColor(context, R.color.purple_500)),
                        topLeft = Offset(rotatedBox.left, rotatedBox.top),
                        size = Size(rotatedBox.width(), rotatedBox.height()),
                        style = Stroke(width = 3.dp.toPx())
                    )

                    // Create text to display
                    val category = detection.categories()[0]
                    val drawableText = "${category.categoryName()} ${
                        String.format(
                            locale = Locale.getDefault(),
                            format = "%.2f",
                            category.score(),
                        )
                    }"

                    drawIntoCanvas { canvas ->
                        val bounds = android.graphics.Rect()
                        textPaint.getTextBounds(drawableText, 0, drawableText.length, bounds)
                        val padding = 8f

                        // label below the box
                        val textX = rotatedBox.left
                        val textY = rotatedBox.top + bounds.height() + padding

                        // background
                        canvas.nativeCanvas.drawRect(
                            textX,
                            rotatedBox.top,
                            textX + bounds.width() + padding,
                            textY,
                            textBackgroundPaint
                        )
                        // text
                        canvas.nativeCanvas.drawText(
                            drawableText,
                            textX + padding / 2,
                            textY - padding / 2,
                            textPaint
                        )
                    }
                }
            }
        }
    }
}