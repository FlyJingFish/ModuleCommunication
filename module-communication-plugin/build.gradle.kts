plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
}

apply("$rootDir/gradle/java_base.gradle")
apply("$rootDir/gradle/java_publish.gradle")

dependencies {
    implementation(gradleApi())
    implementation(localGroovy())
    implementation(libs.gradle)
    implementation(libs.dom4j)
    implementation(libs.gson)
}