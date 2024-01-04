// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        classpath("io.github.FlyJingFish.ModuleCommunication:module-communication-plugin:1.0.0")
    }
}
plugins {
    id("com.android.application") version "8.1.3" apply false
    id("com.android.library") version "8.1.3" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
    id("org.jetbrains.kotlin.jvm") version "1.8.10" apply false
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0-rc-1"
    id("com.google.devtools.ksp") version "1.8.10-1.0.9" apply false
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

val appVersionName = getAppVName()
group = properties["PROJ_GROUP"].toString()
version = appVersionName
nexusPublishing {
    repositories {
        create("Nexus") {
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
            username.set(properties["ossrhUsername"].toString())
            password.set(properties["ossrhPassword"].toString())
        }
    }
}