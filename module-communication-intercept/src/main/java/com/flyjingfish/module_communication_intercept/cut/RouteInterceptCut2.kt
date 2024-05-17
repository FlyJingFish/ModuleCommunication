package com.flyjingfish.module_communication_intercept.cut

import com.flyjingfish.android_aop_annotation.ProceedJoinPoint
import com.flyjingfish.android_aop_annotation.anno.AndroidAopMatchClassMethod
import com.flyjingfish.android_aop_annotation.base.MatchClassMethod
import com.flyjingfish.android_aop_annotation.enums.MatchType
import com.flyjingfish.module_communication_intercept.intercept.RouterInterceptManager

@AndroidAopMatchClassMethod(
    targetClassName = "com.flyjingfish.module_communication_annotation.BaseRouterClass",
    methodName = ["goByPath"],
    type = MatchType.DIRECT_EXTENDS
)
internal class RouteInterceptCut2 : MatchClassMethod{
    override fun invoke(joinPoint: ProceedJoinPoint, methodName: String): Any? {
        RouterInterceptManager.notifyIntercept(joinPoint, joinPoint.args?.get(0) as String,true)
        return null
    }
}