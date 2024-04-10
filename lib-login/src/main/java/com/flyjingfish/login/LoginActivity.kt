package com.flyjingfish.login

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.flyjingfish.module_communication_annotation.ImplementClassUtils
import com.flyjingfish.user.`UserActivity$$Router`
import com.flyjingfish.user.UserHelper

class LoginActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userHelper = ImplementClassUtils.getSingleInstance<UserHelper>(UserHelper::class)
        val user = userHelper.getUser()
        `UserActivity$$Router`.goUserActivity(this,"hahah",user)
        Log.e("user",""+user)
    }
}