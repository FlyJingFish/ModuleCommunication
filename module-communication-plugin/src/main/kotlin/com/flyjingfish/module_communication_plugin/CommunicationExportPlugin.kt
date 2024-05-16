package com.flyjingfish.module_communication_plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class CommunicationExportPlugin : Plugin<Project> {
    override fun apply(project: Project) {

        project.extensions.add("communicationConfig", CommunicationConfig::class.java)

        ApplyExportPlugin().apply(project)
    }
}