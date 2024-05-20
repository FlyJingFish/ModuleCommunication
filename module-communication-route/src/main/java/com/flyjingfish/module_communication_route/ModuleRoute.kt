package com.flyjingfish.module_communication_route

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.flyjingfish.module_communication_annotation.bean.ParamsInfo
import com.flyjingfish.module_communication_annotation.bean.PathInfo
import com.flyjingfish.module_communication_annotation.interfaces.BaseRouterClass
import com.flyjingfish.module_communication_route.bean.ClassInfo
import com.flyjingfish.module_communication_route.utils.Utils
import com.flyjingfish.module_communication_route.utils.putValue

object ModuleRoute {
    private val allRouteClass = mutableMapOf<String, BaseRouterClass>()
    private val allClazz = mutableMapOf<String, ClassInfo?>()
    private var application : Application ?= null

    private fun addRouteClass(moduleName: String, routeClazz: BaseRouterClass) {
        allRouteClass[moduleName] = routeClazz
    }

    fun autoAddAllRouteClass(set: MutableSet<BaseRouterClass>) {
        set.forEach {
            val clazzName = it.javaClass.simpleName
            val key = clazzName.split("\\$\\$")[0]
            addRouteClass(key, it)
        }
    }

    fun setApplication(application : Application){
        this.application = application
    }

    fun getApplication():Application?{
        return application
    }

    /**
     * 只指定路由页面，这种方式会寻找所有模块匹配的路由信息
     */
    fun builder(path: String) = RouteBuilder(path)

    /**
     * 指定 module 名，这样只会在该 module 下寻找匹配的路由信息
     * module 名取自 生成的路由类 的前半部，例如 LibUser$$RouterClass 那就是 LibUser
     */
    fun builder(moduleName: String, path: String) = RouteBuilder(path,moduleName)

    /**
     * 自动解析页面跳转信息
     */
    fun builder(uri: Uri) :RouteBuilder? {
        val path = uri.path
        return if (path != null){
            val builder = builder(path)
            val pathInfo = builder.getInfoByPath()
            if (pathInfo != null){
                Utils.putPathParams(pathInfo,uri,builder)
                builder
            }else{
                null
            }
        }else null
    }


    class RouteBuilder(private val path: String, private val moduleName: String? = null) {
        private val intent = Intent()
        private val paramsMap = mutableMapOf<String, Any?>()
        fun <T> putValue(paramName: String, paramsValue: T?): RouteBuilder {
            if (paramsValue == null){
                paramsMap[paramName] = null
                return this
            }
            paramsMap[paramName] = (paramsValue as Any)
            intent.putValue(paramName,paramsValue)
            return this
        }

        fun getBundle():Bundle?{
            return intent.extras
        }

        /**
         * 有相关路由页面才会返回 Intent 否则返回 null
         */
        fun getIntent(context: Context): Intent? {
            if (context !is Activity){
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val clazzInfo = getClassInfo()
            clazzInfo?.let {
                intent.setClass(context, it.pathInfo.clazz)
            }
            return if (clazzInfo != null){
                intent
            }else null
        }

        /**
         * 有相关路由页面才会返回 Intent 否则返回 null
         * 需要 [ModuleRoute].[setApplication] 来初始化 application.
         */
        fun getIntent(): Intent? {
            val app = application
                ?: throw IllegalArgumentException("请调用 ModuleRoute.setApplication 来初始化 application.")
            return getIntent(app as Context)
        }

        /**
         * 跳转页面
         */
        fun go(context: Context) {
            if (context !is Activity){
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val clazzInfo = getClassInfo()
            clazzInfo?.goRouterClazz?.goByPath(path, paramsMap, true,clazzInfo.pathInfo) {
                intent.setClass(context, clazzInfo.pathInfo.clazz)
                context.startActivity(intent)
            }
        }

        /**
         * 跳转页面,需要 [ModuleRoute].[setApplication] 来初始化 application.
         */
        fun go() {
            val app = application
                ?: throw IllegalArgumentException("请调用 ModuleRoute.setApplication 来初始化 application.")
            go(app as Context)
        }


        /**
         * 根据路径信息获取到对应的 [Class] 类
         */
        fun getClassByPath(): Class<*>? {
            val clazzInfo = getClassInfo()
            return clazzInfo?.pathInfo?.clazz
        }

        /**
         * 根据路径信息获取到对应的 [PathInfo] 类
         */
        fun getInfoByPath(): PathInfo? {
            val clazzInfo = getClassInfo()
            return clazzInfo?.pathInfo
        }

        private fun getClassInfo(): ClassInfo? {
            var clazzInfo = allClazz[path]
            if (clazzInfo == null) {
                if (moduleName != null){
                    val routeClazz = allRouteClass[moduleName]
                    if (routeClazz != null){
                        val pathInfo = routeClazz.getInfoByPath(path)
                        if (pathInfo != null) {
                            clazzInfo = ClassInfo(pathInfo, routeClazz)
                            allClazz[path] = clazzInfo
                        }
                    }
                }else{
                    for ((_, routeClazz) in allRouteClass) {
                        val pathInfo = routeClazz.getInfoByPath(path)
                        if (pathInfo != null) {
                            clazzInfo = ClassInfo(pathInfo, routeClazz)
                            allClazz[path] = clazzInfo
                            break
                        }
                    }
                }
            }

            return clazzInfo
        }
    }

}