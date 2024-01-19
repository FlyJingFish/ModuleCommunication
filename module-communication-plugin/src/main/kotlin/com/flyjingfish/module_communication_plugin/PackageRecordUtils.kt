package com.flyjingfish.module_communication_plugin

import java.io.File

object PackageRecordUtils {
    private val lastRecordPackageMap = mutableMapOf<String,MutableSet<String>>()
    private val exposeResIds = mutableListOf<String>()
    private val exposeAssets = mutableListOf<String>()

    fun recordExposeResIds(ids : MutableList<String>){
        exposeResIds.clear()
        exposeResIds.addAll(ids)
    }

    fun getExposeResIds():MutableList<String>{
        return exposeResIds
    }

    fun recordExposeAssets(ids : MutableList<String>){
        exposeAssets.clear()
        exposeAssets.addAll(ids)
    }

    fun getExposeResAssets():MutableList<String>{
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