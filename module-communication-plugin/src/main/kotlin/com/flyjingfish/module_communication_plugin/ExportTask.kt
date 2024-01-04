package com.flyjingfish.module_communication_plugin

import com.android.build.api.variant.Variant
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileReader
import java.io.FileWriter


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
//        val buildType = variant.buildType
//        val flavorName = variant.flavorName
        searchApiFile(project,variantName)
    }
    private fun searchApiFile(curProject: Project, variantName: String){
        val genFile = curProject.file("${curProject.buildDir}/generated/ksp/${variantName}").listFiles()
        val collection = curProject.files(genFile).asFileTree.filter { it.name.endsWith(".api") }

        val dir = project.project(":${exportModuleName}".replace("\"","")).projectDir
//        val path = "/src/${catalog}/java/com/flyjingfish/instance"
        val path = "build/${LibVersion.pathName}/${variantName}"
        val packageFile = File(dir, path)
        packageFile.deleteRecursively()
        for (file in collection.files) {
            val packageName = getPackageName(file)
            packageName?.let {
                val packagePath = packageFile.absolutePath +"/"+ it.replace(".","/")
                val targetFile = File(packagePath,file.name.replace(".api",""))
                if (targetFile.exists()){
                    targetFile.delete()
                }else{
                    targetFile.parentFile.mkdirs()
                }
                FileInputStream(file).use { inputs ->
                    val bytes = inputs.readAllBytes()
                    val fileStr = String(bytes)
                    val fileWriter = FileWriter(targetFile)
                    val bufferedWriter = BufferedWriter(fileWriter)
                    bufferedWriter.write(fileStr)
                    bufferedWriter.close()
//                    file.delete()
                }

            }
        }
    }

    private fun getPackageName(file:File):String?{
        val br = BufferedReader(FileReader(file))
        var line: String
        var packageName :String ? = null
        while (br.readLine().also { line = it } != null) {
            val code = line.trim { it <= ' ' }
            println(code)
            if (code.startsWith("package")) {
                println(code.split(" ".toRegex())[1])
                packageName = code.split(" ")[1]
                break
            }
        }
        br.close()
        return packageName?.replace(";","")
    }



}