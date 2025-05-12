package com.lucasprioste.mediapipeandroid

import android.app.Application
import com.lucasprioste.mediapipeandroid.di.InitDI
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class MediaPipeAndroidApp: Application() {
    override fun onCreate() {
        super.onCreate()
        InitDI.doInit(
            config = {
                androidLogger()
                androidContext(this@MediaPipeAndroidApp)
            }
        )
    }
}