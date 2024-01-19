package com.flyjingfish.module_communication_plugin

import java.io.File

object PackageRecordUtils {
    private val lastRecordPackageMap = mutableMapOf<String,MutableSet<String>>()
    private val lastRecordResFileMap = mutableMapOf<String,MutableSet<String>>()
    private val lastRecordResXmlMap = mutableMapOf<String,MutableList<ResValueRecord>>()
    private val exposeResIds = mutableListOf<String>()

    fun recordExposeResIds(ids : MutableList<String>){
        exposeResIds.clear()
        exposeResIds.addAll(ids)
    }

    fun getExposeResIds():MutableList<String>{
        return exposeResIds
    }

    fun recordCodeFile(moduleKey :String, packageName :String){
        var lastRecordPackageSet = lastRecordPackageMap[moduleKey]
        if (lastRecordPackageSet == null){
            lastRecordPackageSet = mutableSetOf()
            lastRecordPackageMap[moduleKey] = lastRecordPackageSet
        }
        lastRecordPackageSet.add(packageName)
    }

    fun clearCodeFile(moduleKey :String, buildFile : File):Boolean{
        val lastRecordPackageSet = lastRecordPackageMap[moduleKey]
        lastRecordPackageSet?.let {
//            println("lastRecordPackageSet-size="+it.size);

            for (packageName in it) {
                val packageFile = File("${buildFile.absolutePath}/${packageName.replace(".","/")}")
                packageFile.deleteRecursively()
            }

            it.clear()
        }
        return lastRecordPackageSet.isNullOrEmpty()
    }

    fun recordResFile(moduleKey :String, resFilePath :String){
        var lastRecordPackageSet = lastRecordResFileMap[moduleKey]
        if (lastRecordPackageSet == null){
            lastRecordPackageSet = mutableSetOf()
            lastRecordResFileMap[moduleKey] = lastRecordPackageSet
        }
        lastRecordPackageSet.add(resFilePath)
    }

    fun clearResFile(moduleKey :String,buildFile : File):Boolean{
        val lastRecordPackageSet = lastRecordResFileMap[moduleKey]
        lastRecordPackageSet?.let {
//            println("lastRecordPackageSet-size="+it.size);

            for (resFilePath in it) {
                val packageFile = File("${buildFile.absolutePath}/${resFilePath}")
                packageFile.deleteRecursively()
            }

            it.clear()
        }
        return lastRecordPackageSet.isNullOrEmpty()
    }

    fun recordResXml(moduleKey :String,packageName :ResValueRecord){
        var lastRecordPackageSet = lastRecordResXmlMap[moduleKey]
        if (lastRecordPackageSet == null){
            lastRecordPackageSet = mutableListOf()
            lastRecordResXmlMap[moduleKey] = lastRecordPackageSet
        }
        lastRecordPackageSet.add(packageName)
    }

    fun clearResXml(moduleKey :String,buildFile : File):Boolean{
        val lastRecordPackageSet = lastRecordResXmlMap[moduleKey]
        lastRecordPackageSet?.let {
//            println("lastRecordPackageSet-size="+it.size);

            for (resValueRecord in it) {
                Dom4jData.deleteElementLabel(resValueRecord.xmlFile,resValueRecord.resValue)
            }

            it.clear()
        }
        return lastRecordPackageSet.isNullOrEmpty()
    }

}