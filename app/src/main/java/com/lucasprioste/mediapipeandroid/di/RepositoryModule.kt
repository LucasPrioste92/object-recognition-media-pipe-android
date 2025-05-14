package com.lucasprioste.mediapipeandroid.di

import com.lucasprioste.mediapipeandroid.data.repository.ObjectDetectionRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::ObjectDetectionRepository)
}