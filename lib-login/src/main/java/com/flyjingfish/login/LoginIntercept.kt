package com.flyjingfish.login

import android.util.Log
import com.flyjingfish.module_communication_intercept.intercept.InterceptPoint
import com.flyjingfish.module_communication_intercept.intercept.RouterIntercept

class LoginIntercept : RouterIntercept {
    override fun onIntercept(proceed: InterceptPoint) {
        Log.e("onIntercept","--LoginIntercept--${proceed.path},params = ${proceed.paramsMap},byPath = ${proceed.byPath}")
        proceed.proceed()
    }

    override fun order(): Int {
        return 1
    }
}