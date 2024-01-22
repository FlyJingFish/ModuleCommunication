package com.flyjingfish.user

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.flyjingfish.login.LoginHelper
import com.flyjingfish.module_communication_annotation.ImplementClassUtils

class UserActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        val loginHelper = ImplementClassUtils.getNewInstance<LoginHelper>(LoginHelper::class)
        val login = loginHelper.getLogin()
        Log.e("login",""+login)
        findViewById<ImageView>(R.id.iv_image).setImageResource(R.drawable.login_logo)
        val text = R.string.login_text
        val theme = R.style.LoginAppTheme
    }
}