package com.flyjingfish.modulecommunication

import android.app.Application

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        CollectApp.onCreate(this)
    }
}