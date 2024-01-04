package com.flyjingfish.login

import com.flyjingfish.module_communication_annotation.ExposeInterface

@ExposeInterface
interface LoginHelper {
    fun getLogin():Login
}