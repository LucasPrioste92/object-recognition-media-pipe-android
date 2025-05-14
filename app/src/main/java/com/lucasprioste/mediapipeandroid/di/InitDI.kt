package com.lucasprioste.mediapipeandroid.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

object InitDI {
    fun doInit(
        config: KoinAppDeclaration? = null,
    ) {
        startKoin {
            config?.invoke(this)
            modules(
                helperModule,
                repositoryModule,
                viewModelModule,
            )
        }
    }
}