package com.lucasprioste.mediapipeandroid.presentation.core.utils

import android.graphics.Matrix
import android.graphics.RectF
import androidx.compose.ui.graphics.Color
import kotlin.math.abs

fun getSolidColorForName(name: String): Color {
    val hash = name.hashCode()

    val r = (abs(hash) % 256) / 255f
    val g = (abs(hash / 256) % 256) / 255f
    val b = (abs(hash / 65536) % 256) / 255f

    val maxChannel = maxOf(r, g, b)
    if (maxChannel < 0.7f) {
        return when {
            r >= g && r >= b -> Color(0.7f + (r * 0.3f), g * 0.8f, b * 0.8f)
            g >= r && g >= b -> Color(r * 0.8f, 0.7f + (g * 0.3f), b * 0.8f)
            else -> Color(r * 0.8f, g * 0.8f, 0.7f + (b * 0.3f))
        }
    }

    return Color(r, g, b)
}

fun Int.getWidthAndHeightBasedOnRotation(width: Int, height: Int) = when (this) {
    90, 270 -> Pair(height, width)
    else -> Pair(width, height)
}

fun RectF.rotationHandling(rotation: Int, width: Int, height: Int) {
    val matrix = Matrix()
    matrix.postTranslate(-width / 2f, -height / 2f)
    matrix.postRotate(rotation.toFloat())
    if (rotation == 90 || rotation == 270) matrix.postTranslate(height / 2f, width / 2f)
    else matrix.postTranslate(width / 2f, height / 2f)
    matrix.mapRect(this)
}