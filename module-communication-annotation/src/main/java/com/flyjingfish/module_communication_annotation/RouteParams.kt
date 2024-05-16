package com.flyjingfish.module_communication_annotation


@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FIELD)
annotation class RouteParams(
    /**
     * 生成的函数的参数名
     */
    val name: String,
    /**
     * 生成的函数的参数是否是可空类型
     */
    val nullable: Boolean = false
)