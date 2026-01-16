// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        classpath("io.github.flyjingfish:modulecommunication-plugin:${rootProject.properties["TestVersion"]}")
//        classpath("io.github.FlyJingFish.AndroidAop:android-aop-plugin:2.0.8")
    }
}
@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    id("com.android.application") version "8.2.2" apply false
    id("com.android.library") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.23" apply false
    id("org.jetbrains.kotlin.jvm") version "1.9.23" apply false
    id("com.vanniktech.maven.publish") version "0.33.0"
    id("com.google.devtools.ksp") version "1.9.23-1.0.20" apply false
    alias(libs.plugins.androidAop.plugin)
}
//apply(plugin = "android.aop")
ext {
    set("sdkVersion",31)
    set("minSdkVersion",21)
}
fun getVersionProperty(propName:String, defValue:String):String {
    val file = file("version.properties")
    var ret = defValue
    if (file.exists() && file.canRead()) {
        val input = java.io.FileInputStream(file)
        val props = java.util.Properties()
        props.load(input)
        ret = props[propName].toString()
        input.close()
    }
    return ret
}


fun getAppVName(): String {
    return getVersionProperty("PROJ_VERSION", "1.0.0")
}


fun getAppVCode():Int {
    val versionName = getAppVName()
    val versions = versionName.split(".")
    var updateVersionString = ""
    for ((i, item) in versions.withIndex()) {
        val subString = item
        if (i == 0) {
            updateVersionString += subString
            continue
        } else if (i >= 3) {
            break
        }
        val subNumber = Integer.parseInt(subString)
        updateVersionString += String.format("%01d", subNumber)
    }
    return updateVersionString.toInt()
}

task ("bumpVersion") {
    doLast {
        val versionName = getAppVCode() + 1
        val str = versionName.toString()
        val length = str.length
        var newVersionName = ""
        for (i in 0 until length) {
            newVersionName += str.get(i)
            if (i < 2) {
                newVersionName += "."
            }
        }

        val versionPropsFile = file("version.properties")
        val versionProps = java.util.Properties()
        versionProps.load(java.io.FileInputStream(versionPropsFile))
        val oldVersionName :String= versionProps["PROJ_VERSION"].toString()
        versionProps["PROJ_VERSION"] = newVersionName
        versionProps.store(versionPropsFile.outputStream(), null)

        updateREADME("README.md",oldVersionName,newVersionName)
        updateLib("module-communication-plugin/src/main/kotlin/com/flyjingfish/module_communication_plugin/LibVersion.kt",oldVersionName,newVersionName)

        val gradleFile = File("gradle.properties")
        val gradleText = gradleFile.readText()
        val text2 = gradleText.replace("TestVersion = $oldVersionName",
            "TestVersion = $newVersionName"
        )


        gradleFile.writeText(text2)
        println("升级版本号完成，versionName = $newVersionName")
    }
}

fun updateLib(readme :String,oldVersionName :String,newVersionName :String) {
    val configFile = File(readme)
    val exportText = configFile.readText()
    val text = exportText.replace("const val version = \"$oldVersionName\"","const val version = \"$newVersionName\"")
    configFile.writeText(text)
}

fun updateREADME(readme :String,oldVersionName :String,newVersionName :String) {
    val configFile = File(readme)
    val exportText = configFile.readText()
    var text = exportText.replace("io.github.flyjingfish:modulecommunication-plugin:$oldVersionName",
        "io.github.flyjingfish:modulecommunication-plugin:$newVersionName"
    )
    text = text.replace("io.github.flyjingfish:modulecommunication-annotation:$oldVersionName",
        "io.github.flyjingfish:modulecommunication-annotation:$newVersionName"
    )
    text = text.replace("io.github.flyjingfish:modulecommunication-intercept:$oldVersionName",
        "io.github.flyjingfish:modulecommunication-intercept:$newVersionName"
    )
    text = text.replace("io.github.flyjingfish:modulecommunication-route:$oldVersionName",
        "io.github.flyjingfish:modulecommunication-route:$newVersionName"
    )
    configFile.writeText(text)
}
val appVersionName = getAppVName()
group = properties["PROJ_GROUP"].toString()
version = appVersionName