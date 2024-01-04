package com.flyjingfish.module_communication_plugin

import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class CommunicationModulePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)
        project.dependencies.add("implementation","io.github.FlyJingFish.ModuleCommunication:module-communication-annotation:${LibVersion.version}")
        androidComponents.onVariants { variant ->

            variant.sources.java?.let { java ->
                val file = File("${project.buildDir}/${LibVersion.pathName}/${variant.name}")
                if (!file.exists()){
                    file.mkdirs()
                }
                java.addStaticSourceDirectory("build/${LibVersion.pathName}/${variant.name}")
            }
        }
    }
}