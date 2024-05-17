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

    @delegate:RouteParams("params1")
    val params1 :String ? by lazy(LazyThreadSafetyMode.NONE) {
        intent.getStringExtra("params1")
    }

    @delegate:RouteParams("params2",nullable = true)
    val params2 :User ? by lazy(LazyThreadSafetyMode.NONE) {
        val result = intent.getSerializableExtra("params2")
        if (result != null){
            result as User
        }else{
            null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        val loginHelper = ImplementClassUtils.getNewInstance<LoginHelper>(LoginHelper::class)
        val login = loginHelper?.getLogin()
        Log.e("login",""+login)
        findViewById<ImageView>(R.id.iv_image).setImageResource(R.drawable.login_logo)
        val text = R.string.login_text
        val theme = R.style.LoginAppTheme
        val bundle = Bundle()


        Log.e("UserActivity","params1=$params1,params2=$params2")
    }
}