package com.flyjingfish.module_communication_intercept.cut

import android.content.Intent
import com.flyjingfish.android_aop_annotation.ProceedJoinPoint
import com.flyjingfish.android_aop_annotation.anno.AndroidAopMatchClassMethod
import com.flyjingfish.android_aop_annotation.base.MatchClassMethod
import com.flyjingfish.android_aop_annotation.enums.MatchType
import com.flyjingfish.module_communication_annotation.bean.PathInfo
import com.flyjingfish.module_communication_intercept.intercept.Proceed
import com.flyjingfish.module_communication_intercept.RouterInterceptManager

@AndroidAopMatchClassMethod(
    targetClassName = "com.flyjingfish.module_communication_annotation.interfaces.BaseRouterClass",
    methodName = ["goByPath"],
    type = MatchType.DIRECT_EXTENDS
)
internal class RouteInterceptCut : MatchClassMethod{
    override fun invoke(joinPoint: ProceedJoinPoint, methodName: String): Any? {
        val path = joinPoint.args?.get(0)
        val map = joinPoint.args?.get(1)
        val byPath = joinPoint.args?.get(2)
        val pathInfo = joinPoint.args?.get(3)
        val intent = joinPoint.args?.get(4)
        val proceed = Proceed(joinPoint,path as String,map as MutableMap<String,Any?>,pathInfo as PathInfo,byPath as Boolean,intent as Intent)
        RouterInterceptManager.notifyIntercept(proceed)
        return null
    }
}