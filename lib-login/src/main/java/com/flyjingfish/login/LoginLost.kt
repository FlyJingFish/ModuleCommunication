package com.flyjingfish.login

import android.util.Log
import com.flyjingfish.module_communication_route.lost.LostPoint
import com.flyjingfish.module_communication_route.lost.RouterLost

class LoginLost : RouterLost{
    override fun order(): Int {
        return 1
    }

    override fun onLost(lostPoint: LostPoint) {
        Log.e("onLost","--LoginLost--")
        lostPoint.proceed()
    }
}