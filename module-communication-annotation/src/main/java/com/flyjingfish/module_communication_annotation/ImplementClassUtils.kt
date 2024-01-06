package com.flyjingfish.module_communication_annotation

import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

object ImplementClassUtils {
    private val singleBean = ConcurrentHashMap<Class<out Any>,Any>()

    fun <T> getNewInstance(key:Class<out Any>):T{
        val clazzName = CommunicationPackage.BIND_CLASS_PACKAGE + "." + key.simpleName +"\$\$BindClass"
        val clazz = try {
            Class.forName(clazzName) as Class<out Any>
        } catch (e: ClassNotFoundException) {
            throw RuntimeException("没有找到 ${key.name} 的实现类")
        }
        val bindClass = clazz.getDeclaredConstructor()?.newInstance() as BindClass<*>
        val instance = bindClass.getImplementClassInstance()
        return instance as T
    }

    fun <T> getSingleInstance(key:Class<out Any>):T{
        var instance = singleBean[key]
        if (instance == null){
            instance = getNewInstance(key)
            val oldInstance = singleBean.putIfAbsent(key,instance as Any)
            if (oldInstance != null){
                instance = oldInstance
            }
        }
        return instance as T
    }

    fun <T> getNewInstance(key:KClass<out Any>):T{
        return getNewInstance(key.java)
    }

    fun <T> getSingleInstance(key: KClass<out Any>):T{
        return getSingleInstance(key.java)
    }
}