package com.empire.sente

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SenteApplication: Application() {
    override fun onCreate() {
        super.onCreate()
    }
}