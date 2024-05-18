package com.flyjingfish.module_communication_route

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import com.flyjingfish.module_communication_annotation.bean.PathInfo
import com.flyjingfish.module_communication_annotation.interfaces.BaseRouterClass
import com.flyjingfish.module_communication_route.bean.ClassInfo
import java.io.Serializable

object ModuleRoute {
    private val allRouteClass = mutableMapOf<String, BaseRouterClass>()
    private val allClazz = mutableMapOf<String, ClassInfo?>()

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

    /**
     * 只指定路由页面，这种方式会寻找所有模块匹配的路由信息
     */
    fun builder(path: String) = RouteBuilder(path)

    /**
     * 指定 module 名，这样只会在该 module 下寻找匹配的路由信息
     * module 名取自 生成的路由类 的前半部，例如 LibUser$$RouterClass 那就是 LibUser
     */
    fun builder(moduleName: String, path: String) = RouteBuilder(path,moduleName)

    class RouteBuilder(private val path: String, private val moduleName: String? = null) {
        private val intent = Intent()
        private val paramsMap = mutableMapOf<String, Any?>()
        fun <T> putValue(paramName: String, paramsValue: T): RouteBuilder {
            paramsMap[paramName] = (paramsValue as Any)
            when (paramsValue) {
                is Char -> {
                    intent.putExtra(paramName, paramsValue)
                }

                is CharArray -> {
                    intent.putExtra(paramName, paramsValue)
                }

                is Byte -> {
                    intent.putExtra(paramName, paramsValue)
                }

                is ByteArray -> {
                    intent.putExtra(paramName, paramsValue)
                }

                is Short -> {
                    intent.putExtra(paramName, paramsValue)
                }

                is ShortArray -> {
                    intent.putExtra(paramName, paramsValue)
                }

                is Int -> {
                    intent.putExtra(paramName, paramsValue)
                }

                is IntArray -> {
                    intent.putExtra(paramName, paramsValue)
                }

                is Long -> {
                    intent.putExtra(paramName, paramsValue)
                }

                is LongArray -> {
                    intent.putExtra(paramName, paramsValue)
                }

                is Float -> {
                    intent.putExtra(paramName, paramsValue)
                }

                is FloatArray -> {
                    intent.putExtra(paramName, paramsValue)
                }

                is Double -> {
                    intent.putExtra(paramName, paramsValue)
                }

                is DoubleArray -> {
                    intent.putExtra(paramName, paramsValue)
                }

                is Boolean -> {
                    intent.putExtra(paramName, paramsValue)
                }

                is BooleanArray -> {
                    intent.putExtra(paramName, paramsValue)
                }

                is String -> {
                    intent.putExtra(paramName, paramsValue)
                }

                is Array<*> -> {
                    if (paramsValue.isArrayOf<Parcelable>()) {
                        intent.putExtra(paramName, paramsValue as Array<out Parcelable>)
                    } else if (paramsValue.isArrayOf<CharSequence>()) {
                        intent.putExtra(paramName, paramsValue as Array<out CharSequence>)
                    } else if (paramsValue.isArrayOf<String>()) {
                        intent.putExtra(paramName, paramsValue as Array<out String>)
                    }
                }

                is Bundle -> {
                    intent.putExtra(paramName, paramsValue)
                }

                is Serializable -> {
                    intent.putExtra(paramName, paramsValue)
                }

                is Parcelable -> {
                    intent.putExtra(paramName, paramsValue)
                }
            }
            return this
        }

        /**
         * 有相关路由页面才会返回 Intent 否则返回 null
         */
        fun getIntent(context: Context): Intent? {
            val clazzInfo = getClassInfo()
            clazzInfo?.let {
                intent.setClass(context, it.pathInfo.clazz)
            }
            return if (clazzInfo != null){
                intent
            }else null
        }

        /**
         *
         */
        fun go(context: Context) {
            val clazzInfo = getClassInfo()
            clazzInfo?.goRouterClazz?.goByPath(path, paramsMap, true,clazzInfo.pathInfo) {
                intent.setClass(context, clazzInfo.pathInfo.clazz)
                context.startActivity(intent)
            }
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