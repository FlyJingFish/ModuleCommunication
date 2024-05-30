package com.flyjingfish.module_communication_route

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import androidx.fragment.app.Fragment
import com.flyjingfish.module_communication_annotation.bean.ParamsInfo
import com.flyjingfish.module_communication_annotation.bean.PathInfo
import com.flyjingfish.module_communication_annotation.enums.PathType
import com.flyjingfish.module_communication_annotation.interfaces.BaseRouterClass
import com.flyjingfish.module_communication_annotation.interfaces.NewAny
import com.flyjingfish.module_communication_route.bean.ClassInfo
import com.flyjingfish.module_communication_route.bean.NavigationResult
import com.flyjingfish.module_communication_route.callback.OnGoActivity
import com.flyjingfish.module_communication_route.callback.OnNavigationBack
import com.flyjingfish.module_communication_route.lost.RouterLostManager
import com.flyjingfish.module_communication_route.utils.Utils
import com.flyjingfish.module_communication_route.utils.putValue
import java.io.Serializable
import java.util.ArrayList
import kotlin.reflect.KClass

object ModuleRoute {
    private val allRouteClass = mutableMapOf<String, BaseRouterClass>()
    private val allClazz = mutableMapOf<String, ClassInfo?>()
    private var application: Application? = null
    private val handler = Handler(Looper.getMainLooper())

    fun addRouteClass(moduleName: String, routeClazz: BaseRouterClass) {
        allRouteClass[moduleName] = routeClazz
    }

    fun autoAddAllRouteClass(set: MutableSet<BaseRouterClass>) {
        set.forEach {
            val clazzName = it.javaClass.simpleName
            val key = clazzName.split("\\$\\$")[0]
            addRouteClass(key, it)
        }
    }

    fun setApplication(application: Application) {
        this.application = application
    }

    fun getApplication(): Application? {
        return application
    }

    /**
     * 只指定路由页面，这种方式会寻找所有模块匹配的路由信息
     */
    fun builder(path: String) = RouteBuilder(path)

    /**
     * 指定 module 名，这样只会在该 module 下寻找匹配的路由信息
     * module 名取自 生成的路由类 的前半部，例如 LibUser$$RouterClass 那就是 LibUser
     */
    fun builder(moduleName: String, path: String) = RouteBuilder(path, moduleName)

    /**
     * 自动解析 [Uri] 的页面跳转信息，之后直接调用 [RouteBuilder.go]
     */
    fun builder(uri: Uri): RouteBuilder? {
        val path = uri.path
        return if (path != null) {
            val builder = builder(path)
            val pathInfo = builder.getInfoByPath()
            if (pathInfo != null) {
                Utils.putPathParams(pathInfo, uri, builder)
                builder
            } else {
                null
            }
        } else null
    }


    class RouteBuilder(path: String, private val moduleName: String? = null) {
        private val usePath: String = if (path.firstOrNull()?.toString() != "/") {
            "/$path"
        } else {
            path
        }

        private val intent = Intent()
        private val paramsMap = mutableMapOf<String, Any?>()
        private var classInfo : ClassInfo ?= null
        private var isSearchClassInfo = false
        private var onGoActivity: OnGoActivity ?= null

        fun getPath(): String {
            return usePath
        }

        fun getParamsMap(): MutableMap<String, Any?> {
            return paramsMap
        }

        fun getBundle(): Bundle? {
            return intent.extras
        }

        /**
         * 跳转页面
         * @param context 上下文参数
         * @param onNavigationBack 返回跳转结果
         */
        fun go(context: Context, onNavigationBack: OnNavigationBack ?= null):Any? {
            val clazzInfo = getClassInfo()

            val found = clazzInfo != null
            onNavigationBack?.onResult(NavigationResult(found,this,context))
            if (!found){
                RouterLostManager.notifyRouterLost(context,this)
            }
            if (clazzInfo != null && clazzInfo.pathInfo.type == PathType.ACTIVITY){
                intent.setClass(context, clazzInfo.pathInfo.clazz.java)
                if (context !is Activity) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                clazzInfo.goRouterClazz.goByPath(
                    usePath,
                    paramsMap,
                    true,
                    clazzInfo.pathInfo,
                    intent
                ) {
                    goActivity(context, intent)
                }
            }else if (clazzInfo != null && clazzInfo.pathInfo.type == PathType.FRAGMENT){
                val instance = clazzInfo.pathInfo.newInstance()
                if (instance is Fragment) {
                    instance.arguments = intent.extras
                }else if (instance is android.app.Fragment){
                    instance.arguments = intent.extras
                }
                return instance
            }

            return null
        }

        private fun goActivity(context: Context,intent: Intent){
            if (Looper.getMainLooper() == Looper.myLooper()){
                val onGoActivity = this.onGoActivity
                if (onGoActivity?.onGo(context, intent) == true){
                    return
                }
                context.startActivity(intent)
            }else{
                handler.post {
                    val onGoActivity = this.onGoActivity
                    if (onGoActivity?.onGo(context, intent) == true){
                        return@post
                    }
                    context.startActivity(intent)
                }
            }
        }

        /**
         * 设置此项之后 你需要自己去写 [Context.startActivity]
         */
        fun setOnGoActivity(onGoActivity: OnGoActivity):RouteBuilder{
            this.onGoActivity = onGoActivity
            return this
        }

        /**
         * 跳转页面，需要 [ModuleRoute.setApplication] 来初始化 application.
         * @param onNavigationBack 返回跳转结果
         */
        fun go(onNavigationBack: OnNavigationBack ?= null):Any? {
            val app = application
                ?: throw IllegalArgumentException("请调用 ModuleRoute.setApplication 来初始化 application.")
            return go(app as Context,onNavigationBack)
        }

        /**
         * 根据路径信息获取到对应的 [Class] 类
         */
        fun getClassByPath(): Class<*>? {
            val clazzInfo = getClassInfo()
            return clazzInfo?.pathInfo?.clazz?.java
        }

        /**
         * 根据路径信息获取到对应的 [Class] 类
         */
        fun getKClassByPath(): KClass<*>? {
            val clazzInfo = getClassInfo()
            return clazzInfo?.pathInfo?.clazz
        }

        /**
         * 根据路径信息获取到对应的 [PathInfo] 类
         */
        fun getInfoByPath(): PathInfo? {
            val clazzInfo = getClassInfo()
            return clazzInfo?.pathInfo
        }

        private fun getClassInfo(): ClassInfo? {
            if (isSearchClassInfo){
                return classInfo
            }
            var clazzInfo = allClazz[usePath]
            if (clazzInfo == null) {
                if (moduleName != null) {
                    val routeClazz = allRouteClass[moduleName]
                    if (routeClazz != null) {
                        val pathInfo = routeClazz.getInfoByPath(usePath)
                        if (pathInfo != null) {
                            clazzInfo = ClassInfo(pathInfo, routeClazz)
                            allClazz[usePath] = clazzInfo
                        }
                    }
                } else {
                    for ((_, routeClazz) in allRouteClass) {
                        val pathInfo = routeClazz.getInfoByPath(usePath)
                        if (pathInfo != null) {
                            clazzInfo = ClassInfo(pathInfo, routeClazz)
                            allClazz[usePath] = clazzInfo
                            break
                        }
                    }
                }
            }
            classInfo = clazzInfo
            isSearchClassInfo = true
            return clazzInfo
        }

        fun putValue(paramName: String, paramsValue: Char?): RouteBuilder {
            return autoPutValue(paramName, paramsValue)
        }

        fun putValue(paramName: String, paramsValue: CharArray?): RouteBuilder {
            return autoPutValue(paramName, paramsValue)
        }

        fun putValue(paramName: String, paramsValue: Byte?): RouteBuilder {
            return autoPutValue(paramName, paramsValue)
        }

        fun putValue(paramName: String, paramsValue: ByteArray?): RouteBuilder {
            return autoPutValue(paramName, paramsValue)
        }

        fun putValue(paramName: String, paramsValue: Short?): RouteBuilder {
            return autoPutValue(paramName, paramsValue)
        }

        fun putValue(paramName: String, paramsValue: ShortArray?): RouteBuilder {
            return autoPutValue(paramName, paramsValue)
        }

        fun putValue(paramName: String, paramsValue: Int?): RouteBuilder {
            return autoPutValue(paramName, paramsValue)
        }

        fun putValue(paramName: String, paramsValue: IntArray?): RouteBuilder {
            return autoPutValue(paramName, paramsValue)
        }

        fun putValue(paramName: String, paramsValue: Long?): RouteBuilder {
            return autoPutValue(paramName, paramsValue)
        }

        fun putValue(paramName: String, paramsValue: LongArray?): RouteBuilder {
            return autoPutValue(paramName, paramsValue)
        }

        fun putValue(paramName: String, paramsValue: Float?): RouteBuilder {
            return autoPutValue(paramName, paramsValue)
        }

        fun putValue(paramName: String, paramsValue: FloatArray?): RouteBuilder {
            return autoPutValue(paramName, paramsValue)
        }

        fun putValue(paramName: String, paramsValue: Double?): RouteBuilder {
            return autoPutValue(paramName, paramsValue)
        }

        fun putValue(paramName: String, paramsValue: DoubleArray?): RouteBuilder {
            return autoPutValue(paramName, paramsValue)
        }

        fun putValue(paramName: String, paramsValue: Boolean?): RouteBuilder {
            return autoPutValue(paramName, paramsValue)
        }

        fun putValue(paramName: String, paramsValue: BooleanArray?): RouteBuilder {
            return autoPutValue(paramName, paramsValue)
        }

        fun putValue(paramName: String, paramsValue: String?): RouteBuilder {
            return autoPutValue(paramName, paramsValue)
        }

        fun putValue(paramName: String, paramsValue: Array<out Parcelable>?): RouteBuilder {
            return autoPutValue(paramName, paramsValue)
        }

        fun putValue(paramName: String, paramsValue: Array<CharSequence>?): RouteBuilder {
            return autoPutValue(paramName, paramsValue)
        }

        fun putValue(paramName: String, paramsValue: Array<String>?): RouteBuilder {
            return autoPutValue(paramName, paramsValue)
        }

        fun putValue(paramName: String, paramsValue: Bundle?): RouteBuilder {
            return autoPutValue(paramName, paramsValue)
        }

        fun putValue(paramName: String, paramsValue: Serializable?): RouteBuilder {
            return autoPutValue(paramName, paramsValue)
        }

        fun putValue(paramName: String, paramsValue: Parcelable?): RouteBuilder {
            return autoPutValue(paramName, paramsValue)
        }

        fun putIntegerArrayListValue(
            paramName: String,
            paramsValue: ArrayList<Int>?
        ): RouteBuilder {
            if (putMapValue(paramName, paramsValue)) {
                intent.putIntegerArrayListExtra(paramName, paramsValue)
            }
            return this
        }

        fun putStringArrayListValue(
            paramName: String,
            paramsValue: ArrayList<String>?
        ): RouteBuilder {
            if (putMapValue(paramName, paramsValue)) {
                intent.putStringArrayListExtra(paramName, paramsValue)
            }
            return this
        }

        fun putCharSequenceArrayListValue(
            paramName: String,
            paramsValue: ArrayList<CharSequence>?
        ): RouteBuilder {
            if (putMapValue(paramName, paramsValue)) {
                intent.putCharSequenceArrayListExtra(paramName, paramsValue)
            }
            return this
        }

        fun putParcelableArrayListValue(
            paramName: String,
            paramsValue: ArrayList<out Parcelable>?
        ): RouteBuilder {
            if (putMapValue(paramName, paramsValue)) {
                intent.putParcelableArrayListExtra(paramName, paramsValue)
            }
            return this
        }

        private fun <T> putMapValue(paramName: String, paramsValue: T?): Boolean {
            if (paramsValue == null) {
                paramsMap[paramName] = null
                return false
            }
            paramsMap[paramName] = (paramsValue as Any)
            return true
        }

        private fun <T> autoPutValue(paramName: String, paramsValue: T?): RouteBuilder {
            if (putMapValue(paramName, paramsValue)) {
                intent.putValue(paramName, paramsValue)
            }
            return this
        }
    }

}