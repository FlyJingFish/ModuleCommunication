package com.flyjingfish.module_communication_plugin

open class CommunicationConfig {
    /**
     * 要copy到的目标 module 名称
     */
    var exportModuleName = ""
//    /**
//     * route要使用的module名称，通常是你当前的module名字，不设置默认使用当前module的名字
//     */
//    var routeModuleName = ""

    /**
     * 要copy的资源id
     */
    val exposeResIds = mutableListOf<String>()

    /**
     * 要copy的 assets
     */
    val exposeAssets = mutableListOf<String>()
}