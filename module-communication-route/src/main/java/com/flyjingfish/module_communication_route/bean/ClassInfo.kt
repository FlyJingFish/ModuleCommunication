package com.flyjingfish.module_communication_route.bean

import com.flyjingfish.module_communication_annotation.bean.PathInfo
import com.flyjingfish.module_communication_annotation.interfaces.BaseRouterClass

internal class ClassInfo(val pathInfo: PathInfo,val goRouterClazz: BaseRouterClass)