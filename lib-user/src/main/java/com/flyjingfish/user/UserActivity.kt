package com.flyjingfish.user

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.flyjingfish.login.LoginHelper
import com.flyjingfish.module_communication_core.BeanUtils

class UserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val loginHelper = BeanUtils.getNewInstance<LoginHelper>(LoginHelper::class.java)
        val login = loginHelper.getLogin()
        Log.e("login",""+login)
    }
}