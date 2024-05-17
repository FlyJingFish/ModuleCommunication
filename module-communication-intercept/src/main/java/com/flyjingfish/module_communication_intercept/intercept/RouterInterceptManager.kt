package com.flyjingfish.module_communication_intercept.intercept

import com.flyjingfish.android_aop_annotation.ProceedJoinPoint

object RouterInterceptManager {
    private val intercepts = mutableSetOf<RouterIntercept>()

    fun addIntercept(intercept: RouterIntercept) {
        intercepts.add(intercept)
    }

    fun addAllIntercept(intercepts: MutableSet<RouterIntercept>) {
        this.intercepts.clear()
        this.intercepts.addAll(intercepts.sortedBy { it.order() })
    }

    fun notifyIntercept(proceed : Proceed) {
        if (intercepts.isEmpty()) {
            return
        }
        val thisIntercepts = mutableSetOf<RouterIntercept>().apply {
            addAll(intercepts)
        }
        val iterator = thisIntercepts.iterator()
        proceed.listener = object : Proceed.OnProceedListener {
            override fun onInvoke() {
                if (iterator.hasNext()) {
                    val intercept = iterator.next()
                    iterator.remove()
                    proceed.hasNext = iterator.hasNext()
                    intercept.onIntercept(proceed)
                }
            }
        }

        proceed.hasNext = thisIntercepts.size > 1
        val intercept = iterator.next()
        iterator.remove()
        intercept.onIntercept(proceed)
    }
}