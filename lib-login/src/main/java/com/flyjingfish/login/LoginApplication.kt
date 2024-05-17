package com.flyjingfish.login

import android.app.Application
import android.util.Log
import com.flyjingfish.base_lib.IApplication

class LoginApplication:IApplication {
    override fun onCreate(application: Application) {
        Log.e("IApplication",""+LoginApplication::class.java.name)
    }
}