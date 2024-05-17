package com.flyjingfish.modulecommunication

import android.app.Application
import android.util.Log
import com.flyjingfish.android_aop_annotation.anno.AndroidAopCollectMethod
import com.flyjingfish.base_lib.IApplication
import com.flyjingfish.module_communication_intercept.intercept.RouterIntercept
import com.flyjingfish.module_communication_intercept.intercept.RouterInterceptManager

object CollectApp {
    private val allRouterIntercept = mutableSetOf<RouterIntercept>()
    private val allIApplication = mutableSetOf<IApplication>()

    @AndroidAopCollectMethod
    @JvmStatic
    fun collectIntercept(sub: RouterIntercept){
        Log.e("CollectIntercept","collectIntercept=$sub")
        allRouterIntercept.add(sub)
    }

    @AndroidAopCollectMethod
    @JvmStatic
    fun collectIApplication(sub: IApplication){
        Log.e("CollectIntercept","collectIApplication=$sub")
        allIApplication.add(sub)
    }

    fun onCreate(application: Application){
        Log.e("CollectIntercept","getAllRouterIntercept-size=${allRouterIntercept.size}")
        RouterInterceptManager.addAllIntercept(allRouterIntercept)
        allIApplication.forEach {
            it.onCreate(application)
        }
    }
}