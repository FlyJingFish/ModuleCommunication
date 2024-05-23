package com.flyjingfish.module_communication_annotation


@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FIELD)
annotation class RouteParams(
    /**
     * 生成的函数的参数名
     */
    val name: String,
    /**
     * 生成的函数的参数是否是可空类型,同时也表示这一项是否为必传项
     */
    val nullable: Boolean = false
)