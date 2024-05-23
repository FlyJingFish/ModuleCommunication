package com.flyjingfish.module_communication_intercept.intercept

interface RouterIntercept {
    fun onIntercept(point: InterceptPoint)
    fun order():Int
}