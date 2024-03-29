package com.flyjingfish.modulecommunication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.flyjingfish.login.LoginActivity
import com.flyjingfish.modulecommunication.databinding.ActivityMainBinding
import com.flyjingfish.user.UserActivity

class MainActivity : ComponentActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        BeanUtils.register(UserHelper::class.java,UserHelperImpl::class.java)
//        BeanUtils.register(LoginHelper::class.java,LoginHelperImpl::class.java)
        binding.btnGoLogin.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
        }
        binding.btnGoUser.setOnClickListener {
            startActivity(Intent(this,UserActivity::class.java))
        }
    }
}