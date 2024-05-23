package com.flyjingfish.login

import android.util.Log
import com.flyjingfish.module_communication_intercept.intercept.InterceptPoint
import com.flyjingfish.module_communication_intercept.intercept.RouterIntercept

class LoginIntercept : RouterIntercept {
    override fun onIntercept(point: InterceptPoint) {
        Log.e("onIntercept","--LoginIntercept--${point.path},params = ${point.paramsMap},byPath = ${point.byPath}")
        point.proceed()
    }

    override fun order(): Int {
        return 1
    }
}