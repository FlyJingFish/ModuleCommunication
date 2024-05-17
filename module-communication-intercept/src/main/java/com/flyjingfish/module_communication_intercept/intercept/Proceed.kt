package com.flyjingfish.module_communication_intercept.intercept

import com.flyjingfish.android_aop_annotation.ProceedJoinPoint

class Proceed(
    private val joinPoint: ProceedJoinPoint,
    val routerMethodName: String,
    val byPath: Boolean = false,
    val paramsMap: MutableMap<String, Any> = mutableMapOf()
) {
    internal var hasNext = false
    internal var listener: OnProceedListener? = null
    fun proceed() {
        if (!hasNext) {
            joinPoint.proceed()
        } else {
            listener?.onInvoke()
        }
    }

    internal interface OnProceedListener {
        fun onInvoke()
    }
}