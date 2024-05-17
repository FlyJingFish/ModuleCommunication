package com.flyjingfish.module_communication_intercept.cut

import com.flyjingfish.android_aop_annotation.ProceedJoinPoint
import com.flyjingfish.android_aop_annotation.anno.AndroidAopMatchClassMethod
import com.flyjingfish.android_aop_annotation.base.MatchClassMethod
import com.flyjingfish.android_aop_annotation.enums.MatchType
import com.flyjingfish.module_communication_intercept.intercept.Proceed
import com.flyjingfish.module_communication_intercept.intercept.RouterInterceptManager

@AndroidAopMatchClassMethod(
    targetClassName = "com.flyjingfish.module_communication_annotation.BaseRouter",
    methodName = ["*"],
    type = MatchType.DIRECT_EXTENDS
)
internal class RouteInterceptCut : MatchClassMethod{
    override fun invoke(joinPoint: ProceedJoinPoint, methodName: String): Any? {
        if (joinPoint.targetMethod.returnType === Void.TYPE){
            val proceed = Proceed(joinPoint,methodName,false)
            RouterInterceptManager.notifyIntercept(proceed)
            return null
        }
        return joinPoint.proceed()
    }
}