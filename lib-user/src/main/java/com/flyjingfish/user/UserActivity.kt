package com.flyjingfish.user

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.flyjingfish.login.LoginHelper
import com.flyjingfish.module_communication_annotation.ImplementClassUtils
import com.flyjingfish.module_communication_annotation.Route
import com.flyjingfish.module_communication_annotation.RouteParams

@Route("user/UserActivity")
class UserActivity : AppCompatActivity() {

    @RouteParams(key = "params1", keyType = String::class)
    lateinit var params1 :String

    @RouteParams(key = "params2", keyType = User::class)
    lateinit var params2 :String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        val loginHelper = ImplementClassUtils.getNewInstance<LoginHelper>(LoginHelper::class)
        val login = loginHelper.getLogin()
        Log.e("login",""+login)
        findViewById<ImageView>(R.id.iv_image).setImageResource(R.drawable.login_logo)
        val text = R.string.login_text
        val theme = R.style.LoginAppTheme
        val bundle = Bundle()
        intent.extras
    }
}