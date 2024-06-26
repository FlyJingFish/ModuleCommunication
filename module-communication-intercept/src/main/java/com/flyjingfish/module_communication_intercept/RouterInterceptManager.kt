package com.flyjingfish.module_communication_intercept

import com.flyjingfish.module_communication_intercept.intercept.InterceptPoint
import com.flyjingfish.module_communication_intercept.intercept.RouterIntercept

object RouterInterceptManager {
    private val intercepts = mutableSetOf<RouterIntercept>()

    /**
     * 添加单个拦截器
     */
    fun addIntercept(intercept: RouterIntercept) {
        intercepts.add(intercept)
        val newIntercept = intercepts.sortedBy { it.order() }
        intercepts.clear()
        intercepts.addAll(newIntercept)
    }

    /**
     * 删除单个拦截器
     */
    fun removeIntercept(intercept: RouterIntercept) {
        intercepts.remove(intercept)
    }

    /**
     * 添加多个拦截器
     */
    fun addAllIntercept(intercepts: MutableSet<RouterIntercept>) {
        RouterInterceptManager.intercepts.clear()
        RouterInterceptManager.intercepts.addAll(intercepts.sortedBy { it.order() })
    }

    internal fun notifyIntercept(point : InterceptPoint) {
        if (intercepts.isEmpty()) {
            point.proceed()
            return
        }
        val thisIntercepts = mutableSetOf<RouterIntercept>().apply {
            addAll(intercepts)
        }
        val iterator = thisIntercepts.iterator()
        point.listener = object : InterceptPoint.OnProceedListener {
            override fun onInvoke() {
                if (iterator.hasNext()) {
                    val intercept = iterator.next()
                    iterator.remove()
                    point.hasNext = iterator.hasNext()
                    intercept.onIntercept(point)
                }
            }
        }

        point.hasNext = thisIntercepts.size > 1
        val intercept = iterator.next()
        iterator.remove()
        intercept.onIntercept(point)
    }
}