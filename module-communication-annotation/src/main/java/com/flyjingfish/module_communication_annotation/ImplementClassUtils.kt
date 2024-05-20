package com.flyjingfish.module_communication_annotation

import com.flyjingfish.module_communication_annotation.interfaces.BindClass
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

object ImplementClassUtils {
    private val singleBean = ConcurrentHashMap<Class<out Any>,Any>()
    private val bindClassMap = ConcurrentHashMap<String, BindClass<*>>()

    /**
     * 结合 [AndroidAOP](https://github.com/FlyJingFish/AndroidAOP) 使用
     */
    fun addBindClass(bindClass :BindClass<*>){
        bindClassMap[bindClass::class.java.name] = bindClass
    }

    fun <T> getNewInstance(key:Class<out Any>):T?{
        val clazzName = "${key.name}\$\$BindClass"
        val bindClassObj = bindClassMap[clazzName]
        val instance = if (bindClassObj != null){
            bindClassObj.getImplementClassInstance()
        }else{
            val clazz = try {
                Class.forName(clazzName) as Class<out Any>
            } catch (e: ClassNotFoundException) {
                return null
            }
            val bindClass = clazz.getDeclaredConstructor()?.newInstance() as BindClass<*>
            bindClass.getImplementClassInstance()
        }
        return instance as T
    }

    fun <T> getSingleInstance(key:Class<out Any>):T?{
        var instance = singleBean[key]
        if (instance == null){
            instance = getNewInstance(key)
            if (instance == null){
                return null
            }
            val oldInstance = singleBean.putIfAbsent(key, instance)
            if (oldInstance != null){
                instance = oldInstance
            }
        }
        return instance as T
    }

    fun <T> getNewInstance(key:KClass<out Any>):T?{
        return getNewInstance(key.java)
    }

    fun <T> getSingleInstance(key: KClass<out Any>):T?{
        return getSingleInstance(key.java)
    }
}