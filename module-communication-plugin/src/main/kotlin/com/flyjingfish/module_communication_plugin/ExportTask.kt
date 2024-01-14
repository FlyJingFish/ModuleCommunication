package com.flyjingfish.module_communication_plugin

import com.android.build.api.variant.Variant
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.BufferedReader
import java.io.File
import java.io.FileReader


abstract class ExportTask : DefaultTask() {
    init {
        group = "communication"
    }

    @get:Input
    abstract var variant: Variant
    @get:Input
    abstract var exportModuleName: String

    @TaskAction
    fun taskAction() {
        val variantName = variant.name
        searchApiFileAndCopy(project,variantName)
    }
    private fun searchApiFileAndCopy(curProject: Project, variantName: String){
        val genFile = curProject.file("${curProject.buildDir}/generated/ksp/${variantName}").listFiles()
        val collection = curProject.files(genFile).asFileTree.filter { it.name.endsWith(".api") }

        val dir = project.project(":${exportModuleName}".replace("\"","")).projectDir
        val path = "build/${LibVersion.pathName}/${variantName}"
        val buildFile = File(dir, path)

        val moduleKey = curProject.buildDir.absolutePath
        PackageRecordUtils.clear(moduleKey,buildFile)

        for (file in collection.files) {
            val packageName = getPackageName(file)
            packageName?.let {
                PackageRecordUtils.record(moduleKey,it)
                val packagePath = buildFile.absolutePath +"/"+ it.replace(".","/")
                val targetFile = File(packagePath,file.name.replace(".api",""))
                file.copyTo(targetFile,true)
            }
        }
    }

    private fun getPackageName(file:File):String?{
        val br = BufferedReader(FileReader(file))
        var line: String
        var packageName :String ? = null
        while (br.readLine().also { line = it } != null) {
            val code = line.trim { it <= ' ' }
//            println(code)
            if (code.startsWith("package")) {
//                println(code.split(" ".toRegex())[1])
                packageName = code.split(" ")[1]
                break
            }
        }
        br.close()
        return packageName?.replace(";","")
    }



}