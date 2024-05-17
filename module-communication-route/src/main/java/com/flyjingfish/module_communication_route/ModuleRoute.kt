package com.flyjingfish.module_communication_route

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import com.flyjingfish.module_communication_annotation.BaseRouterClass
import com.flyjingfish.module_communication_route.bean.ClassInfo
import java.io.Serializable

object ModuleRoute {
    private val allRouteClass = mutableMapOf<String,BaseRouterClass>()
    private val allClazz = mutableMapOf<String, ClassInfo?>()

    private fun addRouteClass(moduleName:String, routeClazz :BaseRouterClass){
        allRouteClass[moduleName] = routeClazz
    }

    fun autoAddAllRouteClass(set:MutableSet<BaseRouterClass>){
        set.forEach {
            val clazzName = it.javaClass.simpleName
            val key = clazzName.split("\\$\\$")[0]
            addRouteClass(key,it)
        }
    }

    fun builder(path:String) = RouteBuilder(path)

    class RouteBuilder (private val path:String){
        private val intent = Intent()
        private val paramsMap = mutableMapOf<String,Any?>()
        fun <T> putValue(paramName:String,paramsValue:T) : RouteBuilder {
            paramsMap[paramName] = (paramsValue as Any)
            when(paramsValue){
                is Char ->{
                    intent.putExtra(paramName,paramsValue)
                }
                is CharArray ->{
                    intent.putExtra(paramName,paramsValue)
                }
                is Byte ->{
                    intent.putExtra(paramName,paramsValue)
                }
                is ByteArray ->{
                    intent.putExtra(paramName,paramsValue)
                }
                is Short ->{
                    intent.putExtra(paramName,paramsValue)
                }
                is ShortArray ->{
                    intent.putExtra(paramName,paramsValue)
                }
                is Int ->{
                    intent.putExtra(paramName,paramsValue)
                }
                is IntArray ->{
                    intent.putExtra(paramName,paramsValue)
                }
                is Long ->{
                    intent.putExtra(paramName,paramsValue)
                }
                is LongArray ->{
                    intent.putExtra(paramName,paramsValue)
                }
                is Float ->{
                    intent.putExtra(paramName,paramsValue)
                }
                is FloatArray ->{
                    intent.putExtra(paramName,paramsValue)
                }
                is Double ->{
                    intent.putExtra(paramName,paramsValue)
                }
                is DoubleArray ->{
                    intent.putExtra(paramName,paramsValue)
                }
                is Boolean ->{
                    intent.putExtra(paramName,paramsValue)
                }
                is BooleanArray ->{
                    intent.putExtra(paramName,paramsValue)
                }
                is String ->{
                    intent.putExtra(paramName,paramsValue)
                }
                is Array<*> ->{
                    if (paramsValue.isArrayOf<Parcelable>()){
                        intent.putExtra(paramName,paramsValue as Array<out Parcelable>)
                    }else if (paramsValue.isArrayOf<CharSequence>()){
                        intent.putExtra(paramName,paramsValue as Array<out CharSequence>)
                    }else if (paramsValue.isArrayOf<String>()){
                        intent.putExtra(paramName,paramsValue as Array<out String>)
                    }
                }
                is Bundle ->{
                    intent.putExtra(paramName,paramsValue)
                }
                is Serializable ->{
                    intent.putExtra(paramName,paramsValue)
                }
                is Parcelable ->{
                    intent.putExtra(paramName,paramsValue)
                }
            }
            return this
        }

        fun go(context: Context){
            var clazzInfo = allClazz[path]
            if (clazzInfo == null){
                for ((_, routeClazz) in allRouteClass) {
                    val pathClazz = routeClazz.getClassByPath(path)
                    if (pathClazz != null){
                        clazzInfo = ClassInfo(pathClazz,routeClazz)
                        allClazz[path] = clazzInfo
                        break
                    }
                }
            }
            clazzInfo?.goRouterClazz?.goByPath(path,paramsMap,true){
                intent.setClass(context,clazzInfo.clazz)
                context.startActivity(intent)
            }
        }

        fun getClazz():Class<*>?{
            var clazzInfo = allClazz[path]
            if (clazzInfo == null){
                for ((_, routeClazz) in allRouteClass) {
                    val pathClazz = routeClazz.getClassByPath(path)
                    if (pathClazz != null){
                        clazzInfo = ClassInfo(pathClazz,routeClazz)
                        allClazz[path] = clazzInfo
                        break
                    }
                }
            }
            return clazzInfo?.clazz
        }
    }
}