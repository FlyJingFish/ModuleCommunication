package com.flyjingfish.module_communication_plugin

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.Variant
import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

class CommunicationModulePlugin : Plugin<Project> {


    private val variantList: ArrayList<Variant> = ArrayList()
    override fun apply(project: Project) {
        variantList.clear()
        val androidComponents = project.extensions.getByType(AndroidComponentsExtension::class.java)
        project.dependencies.add("implementation","io.github.FlyJingFish.ModuleCommunication:module-communication-annotation:${LibVersion.version}")
        val libraryExtension = project.extensions.getByName("android") as LibraryExtension
        androidComponents.onVariants { variant ->
            variantList.add(variant)
            variant.sources.java?.let { java ->
                val path = "/${LibVersion.buildDir}/${variant.name}/${LibVersion.pathName}"
                val file = File("${project.buildDir}$path")
                if (!file.exists()){
                    file.mkdirs()
                }
                java.addStaticSourceDirectory("build$path")
            }

            variant.sources.assets?.let { assets ->
                val path = "/${LibVersion.buildDir}/${variant.name}/${LibVersion.assetsName}"
                val file = File("${project.buildDir}$path")
                if (!file.exists()){
                    file.mkdirs()
                }
                assets.addStaticSourceDirectory("build$path")
            }

            val path = "/${LibVersion.buildDir}/${variant.name}/${LibVersion.resName}"
            val file = File("${project.buildDir}$path")
            if (!file.exists()){
                file.mkdirs()
            }
            libraryExtension.sourceSets.getByName(variant.name).res.srcDirs("build$path")
//            println("srcDirs="+libraryExtension.sourceSets.getByName(variant.name).res.srcDirs)
//            println("srcDirs="+libraryExtension.sourceSets.names)


//            variant.sources.res?.let { res ->
//                val path = "/${LibVersion.buildDir}/${variant.name}/${LibVersion.resName}"
//                val file = File("${project.buildDir}$path")
//                if (!file.exists()){
//                    file.mkdirs()
//                }
//                val resCreationTask =
//                    project.tasks.register("create${variant.name}Res",ResCreatorTask::class.java){
//                        it.variant = variant
//                    }
//                res.addGeneratedSourceDirectory(resCreationTask,ResCreatorTask::outputDirectory)
//            }
        }

//        project.afterEvaluate {
//            for (variant in variantList) {
//                val variantName = variant.name
//                val variantNameCapitalized = variantName.capitalized()
//                project.tasks.findByName("generateCommunication$variantNameCapitalized")?.finalizedBy("create${variantNameCapitalized}Res")
//            }
//        }
    }
}