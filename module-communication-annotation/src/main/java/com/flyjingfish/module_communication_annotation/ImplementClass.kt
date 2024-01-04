package com.flyjingfish.module_communication_annotation

import kotlin.reflect.KClass


@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class ImplementClass(val clazz : KClass<out Any>)