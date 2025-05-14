package com.lucasprioste.mediapipeandroid.di

import com.lucasprioste.mediapipeandroid.common.ObjectDetectorHelper
import org.koin.dsl.module

val helperModule = module {
    single { ObjectDetectorHelper(context = get()) }
}