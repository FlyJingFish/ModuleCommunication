package com.flyjingfish.module_communication_annotation

interface BaseRouterClass {
    fun getClassByPath(path:String) : Class<*>?
}