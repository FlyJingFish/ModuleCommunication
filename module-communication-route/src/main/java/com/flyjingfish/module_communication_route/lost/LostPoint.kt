package com.flyjingfish.module_communication_route.lost

import android.content.Context
import com.flyjingfish.module_communication_route.ModuleRoute

class LostPoint(
    /**
     * 上下文
     */
    val context: Context,
    /**
     * 路由页面的 [ModuleRoute.RouteBuilder]
     */
    val builder: ModuleRoute.RouteBuilder
) {
    internal var hasNext = false
    internal lateinit var listener: OnProceedListener
    fun proceed() {
        if (hasNext) {
            listener.onInvoke()
        }
    }

    internal interface OnProceedListener {
        fun onInvoke()
    }
}