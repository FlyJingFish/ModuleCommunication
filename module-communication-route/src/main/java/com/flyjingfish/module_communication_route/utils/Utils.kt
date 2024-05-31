package com.flyjingfish.module_communication_route.utils

import android.net.Uri
import android.os.Parcelable
import android.text.TextUtils
import com.flyjingfish.module_communication_annotation.bean.ParamsInfo
import com.flyjingfish.module_communication_annotation.bean.PathInfo
import com.flyjingfish.module_communication_route.ModuleRoute
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable
import java.lang.reflect.Type
import java.util.Collections


internal object Utils {
    /**
     * 拆分查询参数
     *
     * @param rawUri raw uri
     * @return map with params
     */
    private fun splitQueryParameters(rawUri: Uri): Map<String, String?> {
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

    fun putPathParams(pathInfo: PathInfo, rawUri: Uri, builder: ModuleRoute.RouteBuilder) {
        val paramsInfo: List<ParamsInfo> = pathInfo.paramsInfo
        if (paramsInfo.isNotEmpty()) {
            val resultMap: Map<String, String?> = splitQueryParameters(rawUri)
            for (params in paramsInfo) {
                val paramValue = resultMap[params.name]
                paramValue?.let {
                    putValue(params, it, builder)
                }
            }
        }
    }

    private fun putValue(
        paramsInfo: ParamsInfo,
        strValue: String,
        builder: ModuleRoute.RouteBuilder
    ) {
        when (paramsInfo.clazz) {
            String::class -> {
                builder.putValue(paramsInfo.name, strValue)
            }

            Char::class -> {
                builder.putValue(paramsInfo.name, strValue.firstOrNull())
            }

            Byte::class -> {
                builder.putValue(paramsInfo.name, strValue.toByte())
            }

            Short::class -> {
                builder.putValue(paramsInfo.name, strValue.toShort())
            }

            Int::class -> {
                builder.putValue(paramsInfo.name, strValue.toInt())
            }

            Long::class -> {
                builder.putValue(paramsInfo.name, strValue.toLong())
            }

            Float::class -> {
                builder.putValue(paramsInfo.name, strValue.toFloat())
            }

            Double::class -> {
                builder.putValue(paramsInfo.name, strValue.toDouble())
            }

            Boolean::class -> {
                builder.putValue(paramsInfo.name, strValue.toBoolean())
            }

            CharArray::class -> {
                val gson = Gson()
                val data = gson.fromJson<CharArray>(strValue, paramsInfo.clazz.java)
                builder.putValue(paramsInfo.name, data)
            }

            ByteArray::class -> {
                val gson = Gson()
                val data = gson.fromJson<ByteArray>(strValue, paramsInfo.clazz.java)
                builder.putValue(paramsInfo.name, data)
            }

            ShortArray::class -> {
                val gson = Gson()
                val data = gson.fromJson<ShortArray>(strValue, paramsInfo.clazz.java)
                builder.putValue(paramsInfo.name, data)
            }

            IntArray::class -> {
                val gson = Gson()
                val data = gson.fromJson<IntArray>(strValue, paramsInfo.clazz.java)
                builder.putValue(paramsInfo.name, data)
            }

            LongArray::class -> {
                val gson = Gson()
                val data = gson.fromJson<LongArray>(strValue, paramsInfo.clazz.java)
                builder.putValue(paramsInfo.name, data)
            }

            FloatArray::class -> {
                val gson = Gson()
                val data = gson.fromJson<FloatArray>(strValue, paramsInfo.clazz.java)
                builder.putValue(paramsInfo.name, data)
            }

            DoubleArray::class -> {
                val gson = Gson()
                val data = gson.fromJson<DoubleArray>(strValue, paramsInfo.clazz.java)
                builder.putValue(paramsInfo.name, data)
            }

            BooleanArray::class -> {
                val gson = Gson()
                val data = gson.fromJson<BooleanArray>(strValue, paramsInfo.clazz.java)
                builder.putValue(paramsInfo.name, data)
            }
            Array<CharSequence>::class -> {
                val gson = Gson()
                val data = gson.fromJson<Array<CharSequence>>(strValue, paramsInfo.clazz.java)
                builder.putValue(paramsInfo.name, data)
            }
            Array<String>::class -> {
                val gson = Gson()
                val data = gson.fromJson<Array<String>>(strValue, paramsInfo.clazz.java)
                builder.putValue(paramsInfo.name, data)
            }
            ArrayList::class -> {
                val gson = Gson()
                val listType: Type = object : TypeToken<ArrayList<*>>() {}.type
                val data = gson.fromJson<ArrayList<*>>(strValue, listType)
                when(paramsInfo.genericsClazz){
                    Int::class -> {
                        builder.putIntegerArrayListValue(paramsInfo.name, data as java.util.ArrayList<Int>)
                    }
                    String::class -> {
                        builder.putStringArrayListValue(paramsInfo.name, data as java.util.ArrayList<String>)
                    }
                    CharSequence::class -> {
                        builder.putCharSequenceArrayListValue(paramsInfo.name, data as java.util.ArrayList<CharSequence>)
                    }
                    else -> {
                        paramsInfo.genericsClazz?.let {
                            if (Parcelable::class.java.isAssignableFrom(it.java)){
                                builder.putParcelableArrayListValue(paramsInfo.name, data as java.util.ArrayList<Parcelable>)
                            }
                        }

                    }
                }

            }
            else -> {
                val gson = Gson()
                val data = gson.fromJson<Any>(strValue, paramsInfo.clazz.java)
                if (data is Array<*> && data.isArrayOf<Parcelable>()){
                    builder.putValue(paramsInfo.name, data as Array<out Parcelable>)
                }else if (data is Serializable){
                    builder.putValue(paramsInfo.name, data)
                }
            }
        }
    }
}