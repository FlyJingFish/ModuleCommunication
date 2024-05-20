package com.flyjingfish.module_communication_annotation.bean

class PathInfo(val path: String, val clazz: Class<*>, val tag: Int,val paramsInfo: MutableList<ParamsInfo>){

    /**
     * 当前页面的 tag 是否存在 [item]
     */
    fun isExist(item: Int): Boolean {
        return tag and item > 0
    }
}