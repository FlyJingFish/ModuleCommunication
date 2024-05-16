package com.flyjingfish.login

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.flyjingfish.login.databinding.ActivityLoginBinding
import com.flyjingfish.module_communication_annotation.ImplementClassUtils
import com.flyjingfish.user.`LibUser$$Router`
import com.flyjingfish.user.UserHelper

class LoginActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val userHelper = ImplementClassUtils.getSingleInstance<UserHelper>(UserHelper::class)
        val user = userHelper.getUser()
        binding.btnGo.setOnClickListener {
            `LibUser$$Router`.goUser_UserActivity(this,"hahah",user)
        }

        binding.btnGoFragment.setOnClickListener {
            val fragment : Fragment = `LibUser$$Router`.newUser_UserFragment("lalala",user) as Fragment
            supportFragmentManager.beginTransaction().replace(R.id.container,fragment).commit()
        }

        Log.e("user",""+user)


    }
}