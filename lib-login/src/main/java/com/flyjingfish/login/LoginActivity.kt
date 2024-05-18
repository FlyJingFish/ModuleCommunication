package com.flyjingfish.login

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.flyjingfish.login.databinding.ActivityLoginBinding
import com.flyjingfish.module_communication_annotation.ImplementClassUtils
import com.flyjingfish.module_communication_route.ModuleRoute
import com.flyjingfish.user.UserHelper

class LoginActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val userHelper = ImplementClassUtils.getSingleInstance<UserHelper>(UserHelper::class)
        val user = userHelper?.getUser()
        binding.btnGo.setOnClickListener {
//            `LibUser$$Router`.goUser_UserActivity(this,"hahah",null)
            ModuleRoute.builder("user/UserActivity")
                .putValue("params1","lalla")
                .putValue("params2",user)
                .go(this)

        }
        binding.btnGoFragment.setOnClickListener {
            val clazz = ModuleRoute.builder("user/UserFragment")
                .getClassByPath()
            val fragment : Fragment = clazz?.getDeclaredConstructor()?.newInstance() as Fragment
//            val fragment : Fragment = `LibUser$$Router`.newUser_UserFragment("lalala",null) as Fragment
//            supportFragmentManager.beginTransaction().replace(R.id.container,fragment).commit()
        }

        Log.e("user",""+user)


    }
}