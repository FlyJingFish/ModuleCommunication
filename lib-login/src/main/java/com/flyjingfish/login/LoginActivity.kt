package com.flyjingfish.login

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.flyjingfish.login.databinding.ActivityLoginBinding
import com.flyjingfish.module_communication_annotation.ImplementClassUtils
import com.flyjingfish.user.UserHelper
import com.flyjingfish.user.`User_UserActivity$$Router`

class LoginActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val userHelper = ImplementClassUtils.getSingleInstance<UserHelper>(UserHelper::class)
        val user = userHelper.getUser()
        binding.btnGo.setOnClickListener {
            `User_UserActivity$$Router`.goUser_UserActivity(this,"hahah",user)
        }

        Log.e("user",""+user)
    }
}