package com.flyjingfish.module_communication_route.callback

import android.content.Context
import android.content.Intent

/**
 * 设置此项之后 你需要自己去写 [Context.startActivity]
 */
fun interface OnGoActivity {
    fun onGo(context: Context, intent: Intent):Boolean
}