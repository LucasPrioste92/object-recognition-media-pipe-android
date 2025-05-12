package com.lucasprioste.mediapipeandroid.di

import com.lucasprioste.mediapipeandroid.presentation.object_detection_screen.ObjectDetectionViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::ObjectDetectionViewModel)
}