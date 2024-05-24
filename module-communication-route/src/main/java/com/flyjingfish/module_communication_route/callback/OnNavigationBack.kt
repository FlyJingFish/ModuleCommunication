package com.flyjingfish.module_communication_route.callback

import com.flyjingfish.module_communication_route.ModuleRoute

interface OnNavigationBack {
    /**
     * @param found 是否找到路由页面
     * @param routeBuilder 跳转页面时构建的 [ModuleRoute.RouteBuilder]
     */
    fun onResult(found:Boolean,routeBuilder: ModuleRoute.RouteBuilder)
}