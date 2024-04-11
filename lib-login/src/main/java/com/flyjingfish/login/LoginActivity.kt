package com.flyjingfish.login

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.flyjingfish.login.databinding.ActivityLoginBinding
import com.flyjingfish.module_communication_annotation.ImplementClassUtils
import com.flyjingfish.user.UserHelper
import com.flyjingfish.user.`User_UserActivity$$Router`
import com.flyjingfish.user.`User_UserFragment$$Router`

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

        binding.btnGoFragment.setOnClickListener {
            val fragment : Fragment = `User_UserFragment$$Router`.getUser_UserFragment("lalala",user) as Fragment
            supportFragmentManager.beginTransaction().replace(R.id.container,fragment).commit()
        }

        Log.e("user",""+user)
    }
}