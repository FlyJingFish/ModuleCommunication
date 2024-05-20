package com.flyjingfish.module_communication_route.utils

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import java.io.Serializable

fun <T> Intent.putValue(paramName: String, paramsValue: T) {
    val intent: Intent = this
    when (paramsValue) {
        is Char -> {
            intent.putExtra(paramName, paramsValue)
        }

        is CharArray -> {
            intent.putExtra(paramName, paramsValue)
        }

        is Byte -> {
            intent.putExtra(paramName, paramsValue)
        }

        is ByteArray -> {
            intent.putExtra(paramName, paramsValue)
        }

        is Short -> {
            intent.putExtra(paramName, paramsValue)
        }

        is ShortArray -> {
            intent.putExtra(paramName, paramsValue)
        }

        is Int -> {
            intent.putExtra(paramName, paramsValue)
        }

        is IntArray -> {
            intent.putExtra(paramName, paramsValue)
        }

        is Long -> {
            intent.putExtra(paramName, paramsValue)
        }

        is LongArray -> {
            intent.putExtra(paramName, paramsValue)
        }

        is Float -> {
            intent.putExtra(paramName, paramsValue)
        }

        is FloatArray -> {
            intent.putExtra(paramName, paramsValue)
        }

        is Double -> {
            intent.putExtra(paramName, paramsValue)
        }

        is DoubleArray -> {
            intent.putExtra(paramName, paramsValue)
        }

        is Boolean -> {
            intent.putExtra(paramName, paramsValue)
        }

        is BooleanArray -> {
            intent.putExtra(paramName, paramsValue)
        }

        is String -> {
            intent.putExtra(paramName, paramsValue)
        }

        is Array<*> -> {
            if (paramsValue.isArrayOf<Parcelable>()) {
                intent.putExtra(paramName, paramsValue as Array<out Parcelable>)
            } else if (paramsValue.isArrayOf<CharSequence>()) {
                intent.putExtra(paramName, paramsValue as Array<out CharSequence>)
            } else if (paramsValue.isArrayOf<String>()) {
                intent.putExtra(paramName, paramsValue as Array<out String>)
            }
        }

        is Bundle -> {
            intent.putExtra(paramName, paramsValue)
        }

        is Serializable -> {
            intent.putExtra(paramName, paramsValue)
        }

        is Parcelable -> {
            intent.putExtra(paramName, paramsValue)
        }
    }
}