package com.flyjingfish.modulecommunication

import android.app.Application
import android.util.Log
import com.flyjingfish.android_aop_annotation.anno.AndroidAopCollectMethod
import com.flyjingfish.base_lib.IApplication
import com.flyjingfish.module_communication_annotation.ImplementClassUtils
import com.flyjingfish.module_communication_annotation.interfaces.BaseRouterClass
import com.flyjingfish.module_communication_annotation.interfaces.BindClass
import com.flyjingfish.module_communication_intercept.intercept.RouterIntercept
import com.flyjingfish.module_communication_intercept.RouterInterceptManager
import com.flyjingfish.module_communication_route.ModuleRoute
import com.flyjingfish.module_communication_route.lost.RouterLost
import com.flyjingfish.module_communication_route.lost.RouterLostManager

object CollectApp {
    private val allRouterIntercept = mutableSetOf<RouterIntercept>()
    private val allIApplication = mutableSetOf<IApplication>()
    private val allRouteClazz = mutableSetOf<BaseRouterClass>()
    private val allRouterLost = mutableSetOf<RouterLost>()

    /**
     * 这一步才可以收集到所有的拦截器
     */
    @AndroidAopCollectMethod
    @JvmStatic
    fun collectIntercept(sub: RouterIntercept){
        Log.e("CollectIntercept","collectIntercept=$sub")
        allRouterIntercept.add(sub)
    }

    /**
     * 这一步才可以收集到所有的路由路径信息
     */

    @AndroidAopCollectMethod
    @JvmStatic
    fun collectRouterClass(sub: BaseRouterClass){
        Log.e("CollectIntercept","collectRouterClass=$sub")
        allRouteClazz.add(sub)
    }

    /**
     * 收集所有的 module 的 IApplication 类
     */
    @AndroidAopCollectMethod
    @JvmStatic
    fun collectIApplication(sub: IApplication){
        Log.e("CollectIntercept","collectIApplication=$sub")
        allIApplication.add(sub)
    }

    /**
     * 收集所有的 module 的 @ExposeInterface 共享实现类
     */
    @AndroidAopCollectMethod
    @JvmStatic
    fun collectBindClass(sub: BindClass<*>){
        Log.e("CollectIntercept","collectBindClass=$sub")
        ImplementClassUtils.addBindClass(sub)
    }

    /**
     * 收集所有的 module 的 @ExposeInterface 共享实现类
     */
    @AndroidAopCollectMethod
    @JvmStatic
    fun collectRouterLost(sub: RouterLost){
        Log.e("CollectIntercept","collectRouterLost=$sub")
        allRouterLost.add(sub)
    }

    fun onCreate(application: Application){
        Log.e("CollectIntercept","getAllRouterIntercept-size=${allRouterIntercept.size}")
        //设置全部的拦截器让其起作用
        RouterInterceptManager.addAllIntercept(allRouterIntercept)
        //设置全部路由路径信息，这样ModuleRoute才可以起作用
        ModuleRoute.autoAddAllRouteClass(allRouteClazz)
        //循环调用各个 module 的 IApplication.onCreate
        allIApplication.forEach {
            it.onCreate(application)
        }
        //设置 application 之后可以不用传上下文
        ModuleRoute.setApplication(application)
        //设置全部降级策略类
        RouterLostManager.addAllRouterLost(allRouterLost)
    }
}