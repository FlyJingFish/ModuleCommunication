package com.flyjingfish.module_communication_annotation.interfaces

import com.flyjingfish.module_communication_annotation.bean.PathInfo

interface BaseRouterClass {
    fun getInfoByPath(path: String): PathInfo?
    fun goByPath(
        path: String,
        params: MutableMap<String, Any?>,
        byPath: Boolean,
        pathInfo: PathInfo,
        invokeRoute: Runnable
    )
}