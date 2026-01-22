package com.flyjingfish.module_communication_plugin

import com.android.build.gradle.LibraryExtension
import org.gradle.api.Project
import java.io.File
import java.io.Serializable

data class RuntimeProject(
    val projectDir: File,
    val buildDir: File,
    val rootProjectBuildDir: File,
    val layoutBuildDirectory: File,
    val name: String,
    val variantNames: MutableSet<String> = mutableSetOf(),
    val res: MutableMap<String,List<String>> = mutableMapOf(),
    val assets: MutableMap<String,List<String>> = mutableMapOf(),
): Serializable {
    companion object {
        fun get(project: Project): RuntimeProject {
            val runtimeProject = RuntimeProject(
                projectDir = project.projectDir,
                buildDir = project.getBuildDirectory(),
                rootProjectBuildDir = project.rootProject.getBuildDirectory(),
                layoutBuildDirectory = project.layout.buildDirectory.asFile.get(),
                name = project.name
            )
            if (project != project.rootProject){
                project.afterEvaluate {
                    val libraryExtension = project.extensions.getByName("android") as LibraryExtension
                    val variantNames = libraryExtension.sourceSets.names
                    runtimeProject.variantNames.addAll(variantNames)
                    for (name in variantNames) {
                        val res = libraryExtension.sourceSets.getByName(name).res
                        val assets = libraryExtension.sourceSets.getByName(name).assets
                        runtimeProject.res[name]= res.srcDirs.map { it.absolutePath }
                        runtimeProject.assets[name] = assets.srcDirs.map { it.absolutePath }
                    }
                }
            }
            return runtimeProject
        }

    }
}