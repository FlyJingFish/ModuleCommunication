package com.flyjingfish.module_communication_intercept.intercept

import com.flyjingfish.android_aop_annotation.ProceedJoinPoint
import com.flyjingfish.module_communication_annotation.bean.PathInfo

class Proceed(
    private val joinPoint: ProceedJoinPoint,
    /**
     * 路由页面的 path
     */
    val path: String,
    /**
     * 跳转页面时传入的数据
     */
    val paramsMap: MutableMap<String, Any?>,
    /**
     * 跳转页面的 [PathInfo]
     */
    val pathInfo: PathInfo,
    /**
     * true则时通过 ModuleRoute 类跳转的，false则是通过帮助类
     */
    val byPath:Boolean
) {
    internal var hasNext = false
    internal lateinit var listener: OnProceedListener
    fun proceed() {
        if (!hasNext) {
            joinPoint.proceed()
        } else {
            listener.onInvoke()
        }
    }

    internal interface OnProceedListener {
        fun onInvoke()
    }
}