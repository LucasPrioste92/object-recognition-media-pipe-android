package com.lucasprioste.mediapipeandroid.presentation.core.navigation

import kotlinx.serialization.Serializable

sealed class Route {
    @Serializable
    data object ObjectDetectionScreen: Route()
}