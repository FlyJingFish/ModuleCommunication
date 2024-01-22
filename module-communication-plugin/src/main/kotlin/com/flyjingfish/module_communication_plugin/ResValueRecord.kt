package com.flyjingfish.module_communication_plugin

import java.io.File

data class ResValueRecord(val xmlFile:String,val resValue: ResValue){
    fun getXmlFile():File{
        return File(xmlFile)
    }
}