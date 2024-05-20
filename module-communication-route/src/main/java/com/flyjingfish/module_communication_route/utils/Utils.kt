package com.flyjingfish.module_communication_route.utils

import android.net.Uri
import android.text.TextUtils
import com.flyjingfish.module_communication_annotation.bean.ParamsInfo
import com.flyjingfish.module_communication_annotation.bean.PathInfo
import com.flyjingfish.module_communication_route.ModuleRoute
import com.google.gson.Gson
import java.util.Collections

internal object Utils {
    /**
     * 拆分查询参数
     *
     * @param rawUri raw uri
     * @return map with params
     */
    fun splitQueryParameters(rawUri: Uri): Map<String, String?> {
        val query = rawUri.encodedQuery
            ?: return emptyMap()
        val paramMap: MutableMap<String?, String> = LinkedHashMap()
        var start = 0
        do {
            val next = query.indexOf('&', start)
            val end = if (next == -1) query.length else next
            var separator = query.indexOf('=', start)
            if (separator > end || separator == -1) {
                separator = end
            }
            val name = query.substring(start, separator)
            if (!TextUtils.isEmpty(name)) {
                val value = if (separator == end) "" else query.substring(separator + 1, end)
                paramMap[Uri.decode(name)] = Uri.decode(value)
            }

            // Move start to end of name.
            start = end + 1
        } while (start < query.length)
        return Collections.unmodifiableMap(paramMap)
    }
    fun putPathParams(pathInfo: PathInfo, rawUri: Uri, builder : ModuleRoute.RouteBuilder) {
        val paramsInfo: MutableList<ParamsInfo> = pathInfo.paramsInfo
        if (paramsInfo.isNotEmpty()) {
            val resultMap: Map<String, String?> = Utils.splitQueryParameters(rawUri)
            for (params in paramsInfo) {
                val paramValue = resultMap[params.name]
                paramValue?.let {
                    putValue(params,it,builder)
                }
            }
        }
    }
    private fun putValue(paramsInfo: ParamsInfo,strValue : String,builder: ModuleRoute.RouteBuilder){
        when(paramsInfo.className){
            String::class.qualifiedName ,Char::class.qualifiedName->{
                builder.putValue(paramsInfo.name, strValue)
            }
            Byte::class.qualifiedName ->{
                builder.putValue(paramsInfo.name, strValue.toByte())
            }
            Short::class.qualifiedName ->{
                builder.putValue(paramsInfo.name, strValue.toShort())
            }
            Int::class.qualifiedName ->{
                builder.putValue(paramsInfo.name, strValue.toInt())
            }
            Long::class.qualifiedName ->{
                builder.putValue(paramsInfo.name, strValue.toLong())
            }
            Float::class.qualifiedName ->{
                builder.putValue(paramsInfo.name, strValue.toFloat())
            }
            Double::class.qualifiedName ->{
                builder.putValue(paramsInfo.name, strValue.toDouble())
            }
            Boolean::class.qualifiedName ->{
                builder.putValue(paramsInfo.name, strValue.toBoolean())
            }
            else ->{
                val gson = Gson()
                val clazz = Class.forName(paramsInfo.className)
                val data = gson.fromJson<Any>(strValue,clazz)
                builder.putValue(paramsInfo.name, data)
            }

        }
    }
}