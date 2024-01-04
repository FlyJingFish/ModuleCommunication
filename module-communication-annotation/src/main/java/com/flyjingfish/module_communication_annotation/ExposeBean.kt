package com.flyjingfish.module_communication_annotation


@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class ExposeBean
//annotation class ExposeClass(val clazzName:String = "")