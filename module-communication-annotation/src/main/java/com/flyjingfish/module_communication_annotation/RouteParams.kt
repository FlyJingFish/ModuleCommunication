package com.flyjingfish.module_communication_annotation


@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FIELD)
annotation class RouteParams(val name : String)