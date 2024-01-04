package com.flyjingfish.user

import android.util.Log
import com.flyjingfish.module_communication_annotation.ImplementClass

@ImplementClass(UserHelper::class)
class UserHelperImpl :UserHelper {
    override fun getUser():User {
        Log.e("UserHelperImpl","getUser")
        return User("1111")
    }
}