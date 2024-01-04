plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
}

apply("$rootDir/gradle/java_base.gradle")
apply("$rootDir/gradle/java_publish.gradle")

dependencies {
    implementation(project(mapOf("path" to ":module-communication-annotation")))
    implementation(libs.kotlinpoet)
    implementation(libs.ksp.api)
}