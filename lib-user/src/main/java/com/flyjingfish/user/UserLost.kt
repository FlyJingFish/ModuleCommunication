package com.flyjingfish.user

import android.util.Log
import com.flyjingfish.module_communication_route.lost.LostPoint
import com.flyjingfish.module_communication_route.lost.RouterLost

class UserLost : RouterLost{
    override fun order(): Int {
        return 2
    }

    override fun onLost(lostPoint: LostPoint) {
        Log.e("onLost","--UserLost--")
        lostPoint.proceed()
    }
}