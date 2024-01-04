package com.flyjingfish.module_communication_plugin

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.Variant
import com.android.build.gradle.internal.tasks.factory.dependsOn
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.configurationcache.extensions.capitalized

class CommunicationExportPlugin : Plugin<Project> {
    private val variantList: ArrayList<Variant> = ArrayList()
    override fun apply(project: Project) {
        val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)
        project.extensions.add("communicationConfig", CommunicationConfig::class.java)
        val hasKsp = project.plugins.hasPlugin("com.google.devtools.ksp")
        if (!hasKsp){
            project.plugins.apply("com.google.devtools.ksp")
        }
//        project.dependencies.add("implementation","io.github.FlyJingFish.ModuleCommunication:module-communication-annotation:${LibVersion.version}")
//        project.dependencies.add("ksp","io.github.FlyJingFish.ModuleCommunication:module-communication-ksp:${LibVersion.version}")
//        project.dependencies.add("compileOnly",":communication")
        androidComponents.onVariants { variant ->
            variantList.add(variant)
            val communicationConfig = project.extensions.getByType(CommunicationConfig::class.java)
            var moduleName = communicationConfig.exportModuleName
            if (moduleName.isEmpty()){
                moduleName = project.properties["CommunicationModuleName"].toString()
            }
            if (moduleName.isEmpty()){
                throw NullPointerException("请设置 Copy 的目标 moduleName")
            }

            val variantName = variant.name
            val variantNameCapitalized = variantName.capitalized()
            project.tasks.register("generateCommunication$variantNameCapitalized", ExportTask::class.java) {
                it.variant = variant
                it.exportModuleName = moduleName
            }.dependsOn("ksp${variantNameCapitalized}Kotlin")
        }
        project.afterEvaluate {
            for (variant in variantList) {
                val variantName = variant.name
                val variantNameCapitalized = variantName.capitalized()
                project.tasks.findByName("ksp${variantNameCapitalized}Kotlin")?.finalizedBy("generateCommunication$variantNameCapitalized")
            }
        }
    }
}