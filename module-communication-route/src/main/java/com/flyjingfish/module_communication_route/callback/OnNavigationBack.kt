package com.flyjingfish.module_communication_route.callback

import com.flyjingfish.module_communication_route.bean.NavigationResult

interface OnNavigationBack {
    /**
     * @param result 发起路由的结果
     */
    fun onResult(result: NavigationResult)
}