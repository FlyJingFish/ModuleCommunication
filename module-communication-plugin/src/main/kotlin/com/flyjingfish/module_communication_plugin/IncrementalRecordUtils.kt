package com.flyjingfish.module_communication_plugin

import java.io.File

object IncrementalRecordUtils {
    private val lastRecordPackageMap = mutableMapOf<String,MutableSet<String>>()
    private val lastExposeResFileMap = mutableMapOf<String,MutableSet<String>>()
    private val lastExposeResValueMap = mutableMapOf<String,MutableList<ResValueRecord>>()
//    private val exposeResIds = mutableListOf<String>()
    private val exposeAssets = mutableListOf<String>()

//    fun recordExposeResIds(ids : MutableList<String>){
//        exposeResIds.clear()
//        exposeResIds.addAll(ids)
//    }
//
//    fun getExposeResIds():MutableList<String>{
//        return exposeResIds
//    }
    fun recordResFile(moduleKey :String, filePath :String){
        var lastRecordPackageSet = lastExposeResFileMap[moduleKey]
        if (lastRecordPackageSet == null){
            lastRecordPackageSet = mutableSetOf()
            lastExposeResFileMap[moduleKey] = lastRecordPackageSet
        }
        lastRecordPackageSet.add(filePath)
    }
    fun clearResFile(moduleKey :String, buildFile : File):Boolean{
        val lastRecordPackageSet = lastExposeResFileMap[moduleKey]
        lastRecordPackageSet?.let {
//            println("lastRecordPackageSet-size="+it.size);

            for (filePath in it) {
                val packageFile = File("${buildFile.absolutePath}/${filePath}")
                packageFile.deleteRecursively()
            }

            it.clear()
        }
        return lastRecordPackageSet.isNullOrEmpty()
    }
    fun recordResValue(moduleKey :String, resValueRecord: ResValueRecord){
        var lastRecordPackageSet = lastExposeResValueMap[moduleKey]
        if (lastRecordPackageSet == null){
            lastRecordPackageSet = mutableListOf()
            lastExposeResValueMap[moduleKey] = lastRecordPackageSet
        }
        lastRecordPackageSet.add(resValueRecord)
    }
    fun clearResValue(moduleKey :String, buildFile : File):Boolean{
        val lastRecordPackageSet = lastExposeResValueMap[moduleKey]
        lastRecordPackageSet?.let {
            for (resValueRecord in it) {
                val xmlFile = resValueRecord.xmlFile
                Dom4jData.deleteElementLabel(xmlFile,resValueRecord.resValue)
            }

            it.clear()
        }
        return lastRecordPackageSet.isNullOrEmpty()
    }

    fun recordExposeAssets(ids : MutableList<String>){
        exposeAssets.clear()
        exposeAssets.addAll(ids)
    }

    fun getExposeAssets():MutableList<String>{
        return exposeAssets
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

}