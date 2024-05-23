package com.flyjingfish.modulecommunication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.core.net.toUri
import com.flyjingfish.module_communication_annotation.ImplementClassUtils
import com.flyjingfish.module_communication_route.ModuleRoute
import com.flyjingfish.user.TestBean
import com.flyjingfish.user.TestBean2
import com.flyjingfish.user.UserHelper
import com.google.gson.Gson
import java.util.ArrayList
import java.util.Arrays


class SchemeFilterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
                val uri = intent.data
//        val userHelper = ImplementClassUtils.getSingleInstance<UserHelper>(UserHelper::class)
//        val user = userHelper?.getUser()
//        val gson = Gson()
//        val intArray = intArrayOf(1,2,3)
//        val testArray = arrayOf(TestBean(1),TestBean(2))
//        val idArray = ArrayList<String>(listOf("3","3"))
//        val userTestBean2List = ArrayList<TestBean2>(listOf(TestBean2(11), TestBean2(22)))
////        Log.e("SchemeFilterActivity",gson.toJson(intArray))
//        val uriTest = "lightrouter://test.flyjingfish.com/user/DetailActivity?age=10&name=" +
//                "hahahaha&aChar=a&user=${gson.toJson(user)}&userIds=${gson.toJson(intArray)}" +
//                "&userList=${gson.toJson(testArray)}&userIdList=${gson.toJson(idArray)}&userTestBean2List=${gson.toJson(userTestBean2List)}"
//        Log.e("SchemeFilterActivity",uriTest)
//
//        val uri = uriTest.toUri()
        uri?.let {
            ModuleRoute.builder(it)?.go()
            finish()
        }
    }
}