package com.flyjingfish.login

import com.flyjingfish.module_communication_annotation.ImplementClass

@ImplementClass(LoginHelper::class)
class LoginHelperImpl:LoginHelper {
    override fun getLogin(): Login {
        return Login("username:1111")
    }
}