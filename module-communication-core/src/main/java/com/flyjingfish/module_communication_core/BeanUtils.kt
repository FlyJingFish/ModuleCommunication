package com.flyjingfish.module_communication_core

import com.flyjingfish.module_communication_annotation.BindClass
import com.flyjingfish.module_communication_annotation.CommunicationPackage

object BeanUtils {
    private val beanMap = mutableMapOf<Class<out Any>,Class<out Any>>()
    private val singleBean = mutableMapOf<Class<out Any>,Any>()

//    fun register(key:Class<out Any>,value:Class<out Any>){
//        beanMap[key] = value
//    }
//    fun <T> getNewInstance(key:Class<out Any>):T{
//        val clazz = beanMap[key]
//        val instance = clazz?.getConstructor()?.newInstance()
//        return instance as T
//    }
//
//    fun <T> getSingleInstance(key:Class<out Any>):T{
//        var instance = singleBean[key]
//        if (instance == null){
//            instance = getNewInstance(key)
//            singleBean[key] = instance as Any
//        }
//        return instance as T
//    }


//    fun <T> getNewInstance(key:Class<out Any>):T{
//        val clazzName = CommunicationPackage.BIND_CLASS_PACKAGE + "." + key.simpleName +"\$\$BindClass"
//        val clazz = try {
//            Class.forName(clazzName) as Class<out Any>
//        } catch (e: ClassNotFoundException) {
//            throw RuntimeException("没有找到 ${key.name} 的实现类")
//        }
//        if (clazz.isAnnotationPresent(KeepClass::class.java)) {
//            val annotation: Annotation = clazz.getAnnotation(KeepClass::class.java)
//            if (annotation is KeepClass) {
//                val value: String = annotation.clazzName // 获取注解的属性值
//                return Class.forName(value).getConstructor().newInstance() as T
//            }
//        }
//        throw RuntimeException("没有找到 ${key.name} 的实现类")
//    }

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
            singleBean[key] = instance as Any
        }
        return instance as T
    }

}