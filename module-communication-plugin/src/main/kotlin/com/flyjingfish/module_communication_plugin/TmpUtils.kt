package com.flyjingfish.module_communication_plugin

import com.android.build.api.variant.Variant
import com.android.build.gradle.internal.coverage.JacocoReportTask
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.gradle.api.Project
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths


object TmpUtils {
    private lateinit var buildConfigCacheFile: File
    private val gson: Gson = GsonBuilder().create()
    private fun <T> optFromJsonString(jsonString: String, clazz: Class<T>): T? {
        try {
            return gson.fromJson(jsonString, clazz)
        } catch (e: Throwable) {
            JacocoReportTask.JacocoReportWorkerAction.logger.warn("optFromJsonString(${jsonString}, $clazz", e)
        }
        return null
    }

    private fun optToJsonString(any: Any): String {
        try {
            return gson.toJson(any)
        } catch (throwable: Throwable) {
            JacocoReportTask.JacocoReportWorkerAction.logger.warn("optToJsonString(${any}", throwable)
        }
        return ""
    }

    private fun saveFile(file: File, data:String) {
        temporaryDirMkdirs()
        val fos = FileOutputStream(file.absolutePath)
        try {
            fos.write(data.toByteArray())
        } catch (e: IOException) {
            e.printStackTrace();
        }finally {
            fos.close()
        }
    }
    private fun readAsString(path :String) :String {
        return try {
            val content = String(Files.readAllBytes(Paths.get(path)));
            content
        }catch (exception:Exception){
            ""
        }
    }


    fun initTmp(project: Project, variant1: Variant){
        val dir = project.projectDir
        val codePath = "build/${LibVersion.buildDir}/${variant1.name}".replace("/", File.separator)
        val buildFile = File(dir, codePath)
        buildConfigCacheFile = File(buildFile.absolutePath, "tmp.json")
        if (temporaryDirMkdirs()){
            val bean :IncrementalRecord? = optFromJsonString(readAsString(buildConfigCacheFile.absolutePath),IncrementalRecord::class.java)
            IncrementalRecordUtils.init(bean)
        }

    }

    fun exportTmp(){
        val json = GsonBuilder().setPrettyPrinting().create().toJson(IncrementalRecordUtils.getIncrementalRecord())

        saveFile(buildConfigCacheFile,json)
    }

    private fun temporaryDirMkdirs():Boolean{
        if (!buildConfigCacheFile.parentFile.exists()){
            buildConfigCacheFile.parentFile.mkdirs()
            return false
        }
        return true
    }

}