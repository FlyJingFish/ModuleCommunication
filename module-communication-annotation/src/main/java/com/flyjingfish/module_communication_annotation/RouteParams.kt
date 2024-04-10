package com.flyjingfish.module_communication_annotation

import kotlin.reflect.KClass


@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FIELD)
annotation class RouteParams(val key : String,val keyType : KClass<out Any>)