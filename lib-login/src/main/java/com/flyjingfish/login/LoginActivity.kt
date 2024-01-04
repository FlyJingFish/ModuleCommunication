package com.flyjingfish.login

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.flyjingfish.module_communication_annotation.BeanUtils
import com.flyjingfish.user.UserHelper

class LoginActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userHelper = BeanUtils.getSingleInstance<UserHelper>(UserHelper::class.java)
        val user = userHelper.getUser()
        Log.e("user",""+user)
    }
}