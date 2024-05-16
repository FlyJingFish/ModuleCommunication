package com.flyjingfish.module_communication_plugin

open class CommunicationConfig {
    /**
     * 要copy到的目标 module 名称
     */
    var exportModuleName = ""

    /**
     * 对于 @Route 是否导出空的函数，设置为true，route功能就会失效
     */
    var exportEmptyRoute = false

    /**
     * 要copy的资源id
     */
    val exposeResIds = mutableListOf<String>()

    /**
     * 要copy的 assets
     */
    val exposeAssets = mutableListOf<String>()
    override fun toString(): String {
        return "CommunicationConfig(exportModuleName='$exportModuleName', exposeResIds=$exposeResIds, exposeAssets=$exposeAssets)"
    }


}