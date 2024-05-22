package com.flyjingfish.modulecommunication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import com.flyjingfish.login.LoginActivity
import com.flyjingfish.module_communication_intercept.RouterInterceptManager
import com.flyjingfish.module_communication_intercept.intercept.Proceed
import com.flyjingfish.module_communication_intercept.intercept.RouterIntercept
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

        binding.btnGoUri.setOnClickListener {
            RouterInterceptManager.addIntercept(object :RouterIntercept{
                override fun onIntercept(proceed: Proceed) {
                    Log.e("onIntercept","--MainActivity--${proceed.path},params = ${proceed.paramsMap},byPath = ${proceed.byPath}")
                    proceed.proceed()
                }

                override fun order(): Int {
                    return 4
                }

            })
            startActivity(Intent(this,SchemeFilterActivity::class.java))
//            startActivity(Intent(this,WebActivity::class.java))
        }

        Log.e("MainActivity","------${Int::class.qualifiedName}")
    }
}