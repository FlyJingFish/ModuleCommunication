@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.org.jetbrains.kotlin.android)
    id("com.google.devtools.ksp")
}


android {
    namespace = "com.flyjingfish.module_communication_route"

}
apply("$rootDir/gradle/android_base.gradle")
apply("$rootDir/gradle/android_publish.gradle")
dependencies {
    implementation(project(mapOf("path" to ":module-communication-annotation")))
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    api(libs.androidAop.annotation)
    ksp(libs.androidAop.ksp)
}