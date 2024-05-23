package com.flyjingfish.user

import android.util.Log
import com.flyjingfish.module_communication_intercept.intercept.InterceptPoint
import com.flyjingfish.module_communication_intercept.intercept.RouterIntercept

class UserIntercept : RouterIntercept {
    override fun onIntercept(point: InterceptPoint) {
        Log.e("onIntercept","--UserIntercept--${point.path},params = ${point.paramsMap},byPath = ${point.byPath}")
        point.proceed()
    }

    override fun order(): Int {
        return 3
    }
}