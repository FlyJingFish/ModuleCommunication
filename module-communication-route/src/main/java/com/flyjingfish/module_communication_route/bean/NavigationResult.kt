package com.flyjingfish.module_communication_route.bean

import android.content.Context
import com.flyjingfish.module_communication_route.ModuleRoute

class NavigationResult(
    /**
     * 是否找到路由页面
     */
    val found: Boolean,
    /**
     * 跳转页面时构建的 [ModuleRoute.RouteBuilder]
     */
    val routeBuilder: ModuleRoute.RouteBuilder,
    /**
     * 上下文
     */
    val context: Context
)