package com.flyjingfish.module_communication_route.lost

import android.content.Context
import com.flyjingfish.module_communication_route.ModuleRoute


object RouterLostManager {
    private val routerLoses = mutableSetOf<RouterLost>()

    fun addRouterLost(routerLost: RouterLost) {
        routerLoses.add(routerLost)
        val newRouterLoses = routerLoses.sortedBy { it.order() }
        routerLoses.clear()
        routerLoses.addAll(newRouterLoses)
    }
    /**
     * 添加多个降级处理
     */
    fun addAllRouterLost(routerLoses: MutableSet<RouterLost>) {
        this.routerLoses.clear()
        this.routerLoses.addAll(routerLoses.sortedBy { it.order() })
    }

    internal fun notifyRouterLost(context: Context,builder: ModuleRoute.RouteBuilder) {
        if (routerLoses.isEmpty()) {
            return
        }

        val point = LostPoint(context,builder)

        val thisRouterLoses = mutableSetOf<RouterLost>().apply {
            addAll(routerLoses)
        }
        val iterator = thisRouterLoses.iterator()
        point.listener = object : LostPoint.OnProceedListener {
            override fun onInvoke() {
                if (iterator.hasNext()) {
                    val intercept = iterator.next()
                    iterator.remove()
                    point.hasNext = iterator.hasNext()
                    intercept.onLost(point)
                }
            }
        }

        point.hasNext = thisRouterLoses.size > 1
        val routerLost = iterator.next()
        iterator.remove()
        routerLost.onLost(point)
    }
}