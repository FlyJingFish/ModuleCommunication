package com.flyjingfish.module_communication_plugin

data class IncrementalRecord(
    val lastRecordPackageMap: MutableMap<String, MutableSet<String>>,
    val lastExposeResFileMap: MutableMap<String, MutableSet<String>>,
    val lastExposeResValueMap: MutableMap<String, MutableList<ResValueRecord>>,
    val exposeAssets: MutableSet<String>,
)