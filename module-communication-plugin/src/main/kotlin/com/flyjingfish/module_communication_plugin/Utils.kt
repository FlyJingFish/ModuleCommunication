package com.flyjingfish.module_communication_plugin

import org.gradle.api.Project
import java.io.File


fun Project.getBuildDirectory():File{
//    return buildDir
    return project.layout.buildDirectory.get().asFile
}

