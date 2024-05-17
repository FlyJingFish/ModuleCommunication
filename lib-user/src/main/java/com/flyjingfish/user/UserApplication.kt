package com.flyjingfish.user

import android.app.Application
import android.util.Log
import com.flyjingfish.base_lib.IApplication

class UserApplication:IApplication {
    override fun onCreate(application: Application) {
        Log.e("IApplication",""+UserApplication::class.java.name)
    }
}