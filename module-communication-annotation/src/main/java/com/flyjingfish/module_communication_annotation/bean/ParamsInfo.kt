package com.flyjingfish.module_communication_annotation.bean

import kotlin.reflect.KClass

class ParamsInfo(
    /**
     * 参数名
     */
    val name: String,
    /**
     * 参数类型
     */
    val clazz: KClass<*>,
    /**
     * 参数的泛型类型，目前只有 [clazz] = [java.util.ArrayList] 时这一项才有值
     */
    val genericsClazz: KClass<*>?,
    /**
     * 是否可为null
     */
    val nullable: Boolean
)