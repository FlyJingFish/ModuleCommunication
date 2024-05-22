package com.flyjingfish.module_communication_intercept

import com.flyjingfish.module_communication_intercept.intercept.Proceed
import com.flyjingfish.module_communication_intercept.intercept.RouterIntercept

object RouterInterceptManager {
    private val intercepts = mutableSetOf<RouterIntercept>()

    fun addIntercept(intercept: RouterIntercept) {
        intercepts.add(intercept)
        val newIntercept = intercepts.sortedBy { it.order() }
        intercepts.clear()
        intercepts.addAll(newIntercept)
    }

    fun addAllIntercept(intercepts: MutableSet<RouterIntercept>) {
        RouterInterceptManager.intercepts.clear()
        RouterInterceptManager.intercepts.addAll(intercepts.sortedBy { it.order() })
    }

    fun notifyIntercept(proceed : Proceed) {
        if (intercepts.isEmpty()) {
            proceed.proceed()
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