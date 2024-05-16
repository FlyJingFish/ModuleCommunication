plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
//    id("com.google.devtools.ksp")
    id("communication.export")
}
communicationConfig{
    exportModuleName = "communication"
    exposeResIds.addAll(arrayOf(
        "R.layout.activity_user"
    ))
}
android {
    namespace = "com.flyjingfish.user"
    compileSdk = 34

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(project(":base-lib"))
//    compileOnly(project(":communication"))
//    implementation(project(":module-communication-annotation"))
//    ksp(project(":module-communication-ksp"))
}