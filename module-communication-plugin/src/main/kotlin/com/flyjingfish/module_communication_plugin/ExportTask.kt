package com.flyjingfish.module_communication_plugin

import com.android.build.api.variant.Variant
import com.android.build.gradle.LibraryExtension
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.nio.charset.Charset


abstract class ExportTask : DefaultTask() {
    init {
        group = "communication"
    }

    @get:Input
    abstract var variant: Variant
    @get:Input
    abstract var communicationConfig: CommunicationConfig
    @get:Input
    abstract var exportModuleName: String
    @get:Input
    abstract var copyType: CopyType

    enum class CopyType{
        ALL,COPY_RES,COPY_CODE,COPY_ASSETS
    }

    @TaskAction
    fun taskAction() {
        TmpUtils.initTmp(project.project(":${exportModuleName}".replace("\"","")),variant)
        when(copyType){
            CopyType.COPY_RES ->{
                searchResFileAndCopy(project)
            }
            CopyType.COPY_CODE ->{
                searchApiFileAndCopy(project)
            }
            CopyType.COPY_ASSETS ->{
                searchAssetsFileAndCopy(project)
            }
            else -> {
                searchApiFileAndCopy(project)
                searchResFileAndCopy(project)
                searchAssetsFileAndCopy(project)
            }
        }
        TmpUtils.exportTmp()
    }

    fun searchAssetsFileAndCopy(curProject: Project){
        val codePath = "/${LibVersion.buildDir}/${variant.name}/${LibVersion.assetsName}"
        val libraryExtension = project.extensions.getByName("android") as LibraryExtension
        val variantNames = libraryExtension.sourceSets.names

        val moduleKey = curProject.buildDir.absolutePath
        val dir = project.project(":${exportModuleName}".replace("\"","")).projectDir
        val path = "build$codePath"
        val buildFile = File(dir, path)

        val resValuesDel = mutableListOf<String>()
        for (resId in IncrementalRecordUtils.getExposeAssets()) {
            resValuesDel.add(resId)
        }
        if (resValuesDel.isNotEmpty()){
            for (name in variantNames) {
                val assets = libraryExtension.sourceSets.getByName(name).assets
                for (srcDir in assets.srcDirs) {
                    if (srcDir.exists()){
                        for (resValue in resValuesDel) {
                            val targetFile = File("${buildFile.absolutePath}/$resValue")
                            if (targetFile.exists()){
                                targetFile.deleteRecursively()
                            }
                        }
                    }
                }
            }
        }

        val resValues = mutableListOf<String>()
        for (resId in communicationConfig.exposeAssets) {
            resValues.add(resId)
        }
        if (resValues.isEmpty()){
            return
        }
        for (name in variantNames) {
            val res = libraryExtension.sourceSets.getByName(name).assets
            for (srcDir in res.srcDirs) {
                if (srcDir.exists()){
                    for (resValue in resValues) {
                        val targetFile = File("${buildFile.absolutePath}/$resValue")
                        val file = File("${srcDir.absolutePath}/$resValue")
                        if (file.exists()){
                            file.copyRecursively(targetFile,true)
                        }
                    }


                }
            }
        }
        IncrementalRecordUtils.recordExposeAssets(communicationConfig.exposeAssets)
    }

    fun searchResFileAndCopy(curProject: Project){
        val codePath = "/${LibVersion.buildDir}/${variant.name}/${LibVersion.resName}"
        val libraryExtension = project.extensions.getByName("android") as LibraryExtension
        val variantNames = libraryExtension.sourceSets.names

        val moduleKey = curProject.buildDir.absolutePath
        val dir = project.project(":${exportModuleName}".replace("\"","")).projectDir
        val path = "build$codePath"
        val buildFile = File(dir, path)

        IncrementalRecordUtils.clearResFile(moduleKey, buildFile)
        IncrementalRecordUtils.clearResValue(moduleKey, buildFile)

        val resValuesPre = mutableListOf<ResValue>()
        for (resId in communicationConfig.exposeResIds) {
            val res = ResValue(resId,resId.substring(resId.indexOf(".")+1,resId.lastIndexOf(".")),resId.substring(resId.lastIndexOf(".")+1))
            resValuesPre.add(res)
        }
        val resValues = resValuesPre.filter {
            it.id.startsWith("R.")
        }
        if (resValues.isEmpty()){
            return
        }
        for (name in variantNames) {
            val res = libraryExtension.sourceSets.getByName(name).res
            for (srcDir in res.srcDirs) {
                if (srcDir.exists()){
                    val genFile = srcDir.listFiles()
                    for (resValue in resValues) {
                        var color = false
                        if (Dom4jData.fileRes.contains(resValue.dir) || resValue.dir == "color"){//复制文件的
                            val dirs = genFile.filter {
                                it.name.startsWith(resValue.dir)
                            }
                            val collection = curProject.files(dirs).asFileTree.filter { it.name.startsWith(resValue.fileName) }

                            for (file in collection.files) {
                                val copyPath = "${file.parentFile.name}/${file.name}"
                                val targetFile = File("${buildFile.absolutePath}/$copyPath")
                                file.copyTo(targetFile,true)
                                IncrementalRecordUtils.recordResFile(moduleKey,copyPath)
                                if (resValue.dir == "color"){
                                    color = true
                                }
                            }
                        }
                        if(!Dom4jData.fileRes.contains(resValue.dir) && !color){//复制xml里边的值
                            val dirs = genFile.filter {
                                it.name.startsWith("values")
                            }
                            val collection = curProject.files(dirs).asFileTree.filter { it.name.endsWith(".xml") }

                            for (file in collection.files) {
                                val elements = Dom4jData.getXmlFileElements(file) ?: continue
                                for (element in elements) {
                                    val nodeName: String = element.name
                                    val name: String = element.attribute("name").value
                                    if (name == resValue.fileName && nodeName != "item"){
                                        val targetFile = File("${buildFile.absolutePath}/${file.parentFile.name}",file.name)
                                        if (!targetFile.exists()){
                                            targetFile.parentFile?.mkdirs()
                                            targetFile.createNewFile()
                                            targetFile.writeText("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                                                    "<resources xmlns:tools=\"http://schemas.android.com/tools\" xmlns:android=\"http://schemas.android.com/apk/res/android\" xmlns:app=\"http://schemas.android.com/apk/res-auto\">\n" +
                                                    "</resources>", Charset.forName("utf-8"))
                                        }
                                        val resMapValue = Dom4jData.resMap[resValue.dir]
                                        if ((resMapValue != null && nodeName == resMapValue)||resValue.dir == nodeName){
                                            Dom4jData.addElementLabel(targetFile,element,resValue.fileName)
                                            val resValueRecord = ResValueRecord(targetFile.absolutePath,resValue)
                                            IncrementalRecordUtils.recordResValue(moduleKey, resValueRecord)

                                            val text = targetFile.readText(Charset.forName("utf-8")).replace("\\s*[\r\n]+", "");
                                            targetFile.writeText(text,Charset.forName("utf-8"))
                                        }
                                    }
                                }
                            }
                        }
                    }



                }
            }
        }
    }

    private fun searchApiFileAndCopy(curProject: Project){
        val variantName = variant.name
        val codePath = "/${LibVersion.buildDir}/${variant.name}/${LibVersion.pathName}"
        val genFile = curProject.file("${curProject.buildDir}/generated/ksp/${variantName}").listFiles()
        val collection = curProject.files(genFile).asFileTree.filter { it.name.endsWith(".api") }

        val dir = project.project(":${exportModuleName}".replace("\"","")).projectDir
        val path = "build$codePath"
        val buildFile = File(dir, path)

        val moduleKey = curProject.buildDir.absolutePath
        val isClear = IncrementalRecordUtils.clearCodeFile(moduleKey,buildFile)
        if (isClear){
            val recordPackageSet = mutableSetOf<String>()
            for (file in collection.files) {
                val packageName = getPackageName(file)
                packageName?.let {
                    recordPackageSet.add(it)
                }
            }

            for (packageName in recordPackageSet) {
                val packageFile = File(buildFile.absolutePath +"/"+ packageName.replace(".","/"))
                packageFile.deleteRecursively()
            }
        }

        for (file in collection.files) {
            val packageName = getPackageName(file)
            packageName?.let {
                IncrementalRecordUtils.recordCodeFile(moduleKey,it)
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