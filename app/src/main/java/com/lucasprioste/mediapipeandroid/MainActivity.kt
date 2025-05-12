package com.lucasprioste.mediapipeandroid

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.lucasprioste.mediapipeandroid.presentation.core.navigation.NavigationAppHost
import com.lucasprioste.mediapipeandroid.presentation.core.navigation.Route
import com.lucasprioste.mediapipeandroid.presentation.core.ui.theme.MediaPipeAndroidTheme
import org.koin.androidx.compose.KoinAndroidContext

class MainActivity: ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MediaPipeAndroidTheme {
                KoinAndroidContext {
                    Scaffold(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        NavigationAppHost(
                            navController = rememberNavController(),
                            startDestination = Route.ObjectDetectionScreen,
                        )
                    }
                }
            }
        }
    }
}