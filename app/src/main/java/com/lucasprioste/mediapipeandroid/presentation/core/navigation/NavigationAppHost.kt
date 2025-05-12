package com.lucasprioste.mediapipeandroid.presentation.core.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.lucasprioste.mediapipeandroid.presentation.object_detection_screen.ObjectDetectionScreen
import com.lucasprioste.mediapipeandroid.presentation.object_detection_screen.ObjectDetectionViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun NavigationAppHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination: Route,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = { fadeIn(animationSpec = tween(700)) },
        exitTransition = { fadeOut(animationSpec = tween(700)) },
        popEnterTransition = { fadeIn(animationSpec = tween(700)) },
        popExitTransition = { fadeOut(animationSpec = tween(700)) },
    ) {
        composable<Route.ObjectDetectionScreen> {
            val viewModel = koinViewModel<ObjectDetectionViewModel>()
            ObjectDetectionScreen()
        }
    }
}