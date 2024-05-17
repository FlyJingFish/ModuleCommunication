package com.flyjingfish.module_communication_route.route

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import com.flyjingfish.module_communication_annotation.BaseRouterClass
import java.io.Serializable

object ModuleRoute {
    private val allRouteClass = mutableMapOf<String,BaseRouterClass>()
    private val allClazz = mutableMapOf<String,Class<*>>()

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
        fun <T> putValue(paramName:String,paramsValue:T) :RouteBuilder{
            when(paramsValue){
                is Char ->{
                    intent.putExtra(paramName,paramsValue)
                }
                is Byte ->{
                    intent.putExtra(paramName,paramsValue)
                }
                is Short ->{
                    intent.putExtra(paramName,paramsValue)
                }
                is Int ->{
                    intent.putExtra(paramName,paramsValue)
                }
                is Long ->{
                    intent.putExtra(paramName,paramsValue)
                }
                is Float ->{
                    intent.putExtra(paramName,paramsValue)
                }
                is Double ->{
                    intent.putExtra(paramName,paramsValue)
                }
                is Boolean ->{
                    intent.putExtra(paramName,paramsValue)
                }
                is String ->{
                    intent.putExtra(paramName,paramsValue)
                }
                is Array<*> ->{
                    if (paramsValue.isArrayOf<Parcelable>()){
                        intent.putExtra(paramName,paramsValue as Array<out Parcelable>)
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
            var clazz = allClazz[path]
            if (clazz == null){
                for ((moduleName, routeClazz) in allRouteClass) {
                    val pathClazz = routeClazz.getClassByPath(path)
                    if (pathClazz != null){
                        allClazz[path] = pathClazz
                        clazz = pathClazz
                        break
                    }
                }
            }
            if (clazz != null){
                intent.setClass(context,clazz)
                context.startActivity(intent)
            }
        }
    }
}