package com.flyjingfish.module_communication_annotation.bean

import com.flyjingfish.module_communication_annotation.enums.PathType
import com.flyjingfish.module_communication_annotation.interfaces.NewAny
import kotlin.reflect.KClass

class PathInfo(
    val path: String,
    val clazz: KClass<*>,
    val tag: Int,
    val type: PathType,
    private val newAny: NewAny?,
    val paramsInfo: List<ParamsInfo>
) {

    /**
     * 当前页面的 tag 是否存在 [item]
     */
    fun isExist(item: Int): Boolean {
        return tag and item > 0
    }

    fun newInstance():Any? = newAny?.newInstance()
}