package com.flyjingfish.user

import com.flyjingfish.module_communication_annotation.ExposeInterface

@ExposeInterface
interface UserHelper {
    fun getUser():User
}