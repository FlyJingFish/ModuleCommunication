plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
//    id("com.google.devtools.ksp")
    id("communication.export")
}

communicationConfig{
    exportModuleName = "communication"
    exposeResIds.addAll(arrayOf(
        "R.drawable.login_logo",
        "R.string.login_text",
        "R.array.weekname",
        "R.style.AppTheme2",
        "R.id.icon_upi_close",
        "R.color.color_theme",
        "R.color.color_white_both"
    ))
    //直接可以输入 assets 下的文件夹或者文件路径即可
    exposeAssets.addAll(arrayOf(
        "matching",
        "swipe_like"
    ))
}

android {
    namespace = "com.flyjingfish.login"
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
    compileOnly(project(":communication"))
//    implementation(project(":module-communication-annotation"))
//    ksp(project(":module-communication-ksp"))
}