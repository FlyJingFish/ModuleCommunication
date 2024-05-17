package com.flyjingfish.module_communication_intercept.intercept

interface RouterIntercept {
    fun onIntercept(proceed: Proceed)
    fun order():Int
}