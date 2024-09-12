package com.flyjingfish.module_communication_plugin

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.Variant
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.tasks.factory.dependsOn
import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.configurationcache.extensions.capitalized
import java.io.File

class ApplyExportPlugin: Plugin<Project> {
    companion object{
        /**
         * 公共导出的 module 名
         */
        private const val COMMON_MODULE_NAME = "communication.moduleName"

        /**
         * 是否自动在设置 communication.export 插件的 module 自动 compileOnly 入公共的 module（前提是已经设置了 communication.moduleName）
         * 默认 true
         */
        private const val COMMON_AUTO = "communication.antoCompileOnly"
    }

    private val variantList: ArrayList<Variant> = ArrayList()
    private fun applyCommonDependencies(project: Project) {
        val moduleName = project.properties[COMMON_MODULE_NAME]?:""
        val autoStr = project.properties[COMMON_AUTO]?:"true"
        val autoCompileOnly = autoStr == "true"
        if (moduleName.toString().isNotEmpty() && autoCompileOnly){
            project.dependencies.add("compileOnly",project.dependencies.project(mapOf("path" to ":$moduleName")))
        }
    }
    override fun apply(project: Project) {

        variantList.clear()

        val hasKsp = project.plugins.hasPlugin("com.google.devtools.ksp")
        if (!hasKsp){
            project.plugins.apply("com.google.devtools.ksp")
        }
        project.dependencies.add("implementation","io.github.FlyJingFish.ModuleCommunication:module-communication-annotation:${LibVersion.version}")
        project.dependencies.add("ksp","io.github.FlyJingFish.ModuleCommunication:module-communication-ksp:${LibVersion.version}")

        applyCommonDependencies(project)

        val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)
        androidComponents.onVariants { variant ->
            val communicationConfig = project.extensions.getByType(CommunicationConfig::class.java)
            var moduleName = communicationConfig.exportModuleName
            if (moduleName.isEmpty()){
                moduleName = (project.properties[COMMON_MODULE_NAME]?:"").toString()
            }
            if (moduleName.isNotEmpty()){
                variantList.add(variant)
                for ((index,exposeAsset) in communicationConfig.exposeAssets.withIndex()) {
                    if (exposeAsset.substring(0,1) == "/"||exposeAsset.substring(0,1) == "\\"){
                        communicationConfig.exposeAssets[index] = exposeAsset.substring(1).replace('/',
                            File.separatorChar)
                    }
                }
                val variantName = variant.name
                val variantNameCapitalized = variantName.capitalized()
                project.tasks.register("generateCommunicationCode$variantNameCapitalized", ExportTask::class.java) {
                    it.variant = variant
                    it.communicationConfig = communicationConfig
                    it.exportModuleName = moduleName
                    it.copyType = ExportTask.CopyType.COPY_CODE
                }.dependsOn("ksp${variantNameCapitalized}Kotlin")

                project.tasks.register("generateCommunicationRes$variantNameCapitalized", ExportTask::class.java) {
                    it.variant = variant
                    it.communicationConfig = communicationConfig
                    it.exportModuleName = moduleName
                    it.copyType = ExportTask.CopyType.COPY_RES
                }

                project.tasks.register("generateCommunicationAssets$variantNameCapitalized", ExportTask::class.java) {
                    it.variant = variant
                    it.communicationConfig = communicationConfig
                    it.exportModuleName = moduleName
                    it.copyType = ExportTask.CopyType.COPY_ASSETS
                }

                project.tasks.register("generateCommunicationAll$variantNameCapitalized", ExportTask::class.java) {
                    it.variant = variant
                    it.communicationConfig = communicationConfig
                    it.exportModuleName = moduleName
                    it.copyType = ExportTask.CopyType.ALL
                }.dependsOn("ksp${variantNameCapitalized}Kotlin")
            }else{
                project.logger.error("ModuleCommunication提示：没有设置<负责通信>的目标 moduleName")
            }
        }
        project.afterEvaluate {
            val communicationConfig = project.extensions.getByType(CommunicationConfig::class.java)
            val kspExtension = project.extensions.getByType(KspExtension::class.java)
            val android: BaseExtension = project.extensions.getByName("android") as BaseExtension
            kspExtension.arg("routeModuleName",project.name)
            kspExtension.arg("exportEmptyRoute","${communicationConfig.exportEmptyRoute}")
            val packageName = if (android.namespace == null || android.namespace == "null"){
                android.defaultConfig.applicationId.toString()
            }else{
                android.namespace.toString()
            }

            kspExtension.arg("routeModulePackageName",packageName)
            for (variant in variantList) {
                val variantName = variant.name
                val variantNameCapitalized = variantName.capitalized()
                project.tasks.findByName("ksp${variantNameCapitalized}Kotlin")?.finalizedBy("generateCommunicationCode$variantNameCapitalized")
                project.tasks.findByName("pre${variantNameCapitalized}Build")?.finalizedBy("generateCommunicationRes$variantNameCapitalized")
                project.tasks.findByName("pre${variantNameCapitalized}Build")?.finalizedBy("generateCommunicationAssets$variantNameCapitalized")
            }
        }
    }
}