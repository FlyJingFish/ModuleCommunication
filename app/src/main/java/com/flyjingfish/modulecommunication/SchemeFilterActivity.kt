package com.flyjingfish.modulecommunication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.core.net.toUri
import com.flyjingfish.module_communication_annotation.ImplementClassUtils
import com.flyjingfish.module_communication_route.ModuleRoute
import com.flyjingfish.user.TestBean
import com.flyjingfish.user.UserHelper
import com.google.gson.Gson


class SchemeFilterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        val uri = intent.data
        val userHelper = ImplementClassUtils.getSingleInstance<UserHelper>(UserHelper::class)
        val user = userHelper?.getUser()
        val gson = Gson()
        val intArray = intArrayOf(1,2,3)
        val testArray = arrayOf(TestBean(1),TestBean(2))
//        Log.e("SchemeFilterActivity",gson.toJson(intArray))
        val uriTest = "lightrouter://test.flyjingfish.com/user/DetailActivity?age=10&name=hahahaha&aChar=a&user=${gson.toJson(user)}&userIds=${gson.toJson(intArray)}&userList=${gson.toJson(testArray)}"
        Log.e("SchemeFilterActivity",uriTest)

        val uri = uriTest.toUri()
        uri?.let {
            ModuleRoute.builder(it)?.go()
            finish()
        }
    }
}