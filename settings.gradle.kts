pluginManagement {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        google()
        mavenCentral()
    }
}

rootProject.name = "ModuleCommunication"
include(":app")
include(":module-communication-plugin")
include(":module-communication-ksp")
include(":module-communication-annotation")
include(":base-lib")
include(":lib-user")
include(":lib-login")
include(":communication")
include(":communication2")
include(":module-communication-intercept")
