package com.flyjingfish.module_communication_annotation

import kotlin.reflect.KClass


@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FIELD)
annotation class RouteParams(val name : String)