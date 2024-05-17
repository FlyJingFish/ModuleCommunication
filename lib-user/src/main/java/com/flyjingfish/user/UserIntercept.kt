package com.flyjingfish.user

import android.util.Log
import com.flyjingfish.module_communication_intercept.intercept.Proceed
import com.flyjingfish.module_communication_intercept.intercept.RouterIntercept

class UserIntercept : RouterIntercept {
    override fun onIntercept(proceed: Proceed) {
        Log.e("onIntercept","--UserIntercept--${proceed.routerMethodName}")
        proceed.proceed()
    }

    override fun order(): Int {
        return 2
    }
}