plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}
//apply("$rootDir/gradle/android_base.gradle")
//apply("$rootDir/gradle/android_publish.gradle")
android {
    namespace = "com.flyjingfish.module_communication_core"
    compileSdk = 34

    defaultConfig {
        minSdk = 21

    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(project(":module-communication-annotation"))
}