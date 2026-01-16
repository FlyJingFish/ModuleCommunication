package com.flyjingfish.module_communication_plugin

import org.gradle.api.DefaultTask
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
    abstract var runtimeProject :RuntimeProject
    @get:Input
    abstract var variantName: String
    @get:Input
    abstract var communicationConfig: CommunicationConfig
    @get:Input
    abstract var communicationProjectDir: String
    @get:Input
    abstract var copyType: CopyType

    enum class CopyType{
        ALL,COPY_RES,COPY_CODE,COPY_ASSETS
    }

    @TaskAction
    fun taskAction() {
        TmpUtils.initTmp(communicationProjectDir,variantName)
        when(copyType){
            CopyType.COPY_RES ->{
                searchResFileAndCopy(runtimeProject)
            }
            CopyType.COPY_CODE ->{
                searchApiFileAndCopy(runtimeProject)
            }
            CopyType.COPY_ASSETS ->{
                searchAssetsFileAndCopy(runtimeProject)
            }
            else -> {
                searchApiFileAndCopy(runtimeProject)
                searchResFileAndCopy(runtimeProject)
                searchAssetsFileAndCopy(runtimeProject)
            }
        }
        TmpUtils.exportTmp()
    }

    private fun searchAssetsFileAndCopy(curProject: RuntimeProject){
        val codePath = "/${LibVersion.buildDir}/${variantName}/${LibVersion.assetsName}".replace('/', File.separatorChar)
        val variantNames = curProject.variantNames

        val dir = communicationProjectDir
        val path = "build$codePath"
        val buildFile = File(dir, path)

        val resValuesDel = mutableListOf<String>()
        for (resId in IncrementalRecordUtils.getExposeAssets()) {
            resValuesDel.add(resId)
        }
        if (resValuesDel.isNotEmpty()){
            for (name in variantNames) {
                val assetsStr = curProject.assets[name]?:continue
                val assets = assetsStr.map { File(it) }
                for (srcDir in assets) {
                    if (srcDir.exists()){
                        for (resValue in resValuesDel) {
                            val targetFile = File("${buildFile.absolutePath}${File.separator}$resValue")
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
            val assetsStr = curProject.assets[name]?:continue
            val assets = assetsStr.map { File(it) }
            for (srcDir in assets) {
                if (srcDir.exists()){
                    for (resValue in resValues) {
                        val targetFile = File("${buildFile.absolutePath}${File.separator}$resValue")
                        val file = File("${srcDir.absolutePath}${File.separator}$resValue")
                        if (file.exists()){
                            file.copyRecursively(targetFile,true)
                        }
                    }


                }
            }
        }
        IncrementalRecordUtils.recordExposeAssets(communicationConfig.exposeAssets)
    }

    private fun searchResFileAndCopy(curProject: RuntimeProject){
        val codePath = "/${LibVersion.buildDir}/${variantName}/${LibVersion.resName}".replace('/',File.separatorChar)
        val variantNames = curProject.variantNames

        val moduleKey = curProject.buildDir.absolutePath
        val dir = communicationProjectDir
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
            val resStr = curProject.res[name]?:continue
            val res = resStr.map { File(it) }
            for (srcDir in res) {
                if (srcDir.exists()){
                    val genFile = srcDir.listFiles() ?: continue
                    for (resValue in resValues) {
                        var color = false
                        if (Dom4jData.fileRes.contains(resValue.dir) || resValue.dir == "color"){//复制文件的
                            val dirs = genFile.filter {
                                it.name.startsWith(resValue.dir)
                            }
                            val collection = dirs
                                .asSequence()
                                .filter { it.exists() }
                                .flatMap { file ->
                                    when {
                                        file.isDirectory -> file.walkTopDown()
                                        file.isFile -> sequenceOf(file)
                                        else -> emptySequence()
                                    }
                                }.filter { it.name.startsWith(resValue.fileName) }
                            for (file in collection) {
                                val copyPath = "${file.parentFile.name}${File.separator}${file.name}"
                                val targetFile = File("${buildFile.absolutePath}${File.separator}$copyPath")
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
                            val collection = dirs
                                .asSequence()
                                .filter { it.exists() }
                                .flatMap { file ->
                                    when {
                                        file.isDirectory -> file.walkTopDown()
                                        file.isFile -> sequenceOf(file)
                                        else -> emptySequence()
                                    }
                                }.filter { it.name.endsWith(".xml") }
                            for (file in collection) {
                                val elements = Dom4jData.getXmlFileElements(file) ?: continue
                                for (element in elements) {
                                    val nodeName: String = element.name
                                    val name: String = element.attribute("name").value
                                    if (name == resValue.fileName && nodeName != "item"){
                                        val targetFile = File("${buildFile.absolutePath}${File.separator}${file.parentFile.name}",file.name)
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

    private fun searchApiFileAndCopy(curProject: RuntimeProject){
        val codePath = "/${LibVersion.buildDir}/${variantName}/${LibVersion.pathName}".replace('/',File.separatorChar)
        val genFile = File("${curProject.buildDir}${File.separator}generated${File.separator}ksp${File.separator}${variantName}").listFiles()?: return
        val collection = genFile
            .asSequence()
            .filter { it.exists() }
            .flatMap { file ->
                when {
                    file.isDirectory -> file.walkTopDown()
                    file.isFile -> sequenceOf(file)
                    else -> emptySequence()
                }
            }.filter { it.name.endsWith(".api") }

        val dir = communicationProjectDir
        val path = "build$codePath"
        val buildFile = File(dir, path)

        val moduleKey = curProject.buildDir.absolutePath
        val isClear = IncrementalRecordUtils.clearCodeFile(moduleKey,buildFile)
        if (isClear){
            val recordPackageSet = mutableSetOf<String>()
            for (file in collection) {
                val packageName = getPackageName(file)
                packageName?.let {
                    recordPackageSet.add(it)
                }
            }

            for (packageName in recordPackageSet) {
                val packageFile = File(buildFile.absolutePath + File.separator + packageName.replace(".",File.separator))
                packageFile.deleteRecursively()
            }
        }

        for (file in collection) {
            val packageName = getPackageName(file)
            packageName?.let {
                IncrementalRecordUtils.recordCodeFile(moduleKey,it)
                val packagePath = buildFile.absolutePath + File.separator + it.replace(".",
                    File.separator
                )
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