package com.flyjingfish.module_communication_plugin

import java.io.File

object PackageRecordUtils {
    private val lastRecordPackageMap = mutableMapOf<String,MutableSet<String>>()

    fun record(moduleKey :String,packageName :String){
        var lastRecordPackageSet = lastRecordPackageMap[moduleKey]
        if (lastRecordPackageSet == null){
            lastRecordPackageSet = mutableSetOf()
            lastRecordPackageMap[moduleKey] = lastRecordPackageSet
        }
        lastRecordPackageSet.add(packageName)
    }

    fun clear(moduleKey :String,buildFile : File):Boolean{
        val lastRecordPackageSet = lastRecordPackageMap[moduleKey]
        lastRecordPackageSet?.let {
//            println("lastRecordPackageSet-size="+it.size);

            for (packageName in it) {
                val packageFile = File(buildFile.absolutePath +"/"+ packageName.replace(".","/"))
                packageFile.deleteRecursively()
            }

            it.clear()
        }
        return lastRecordPackageSet.isNullOrEmpty()
    }

}