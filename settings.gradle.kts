pluginManagement {

    repositories {
        mavenLocal()
        google()

        mavenCentral()
        gradlePluginPortal()
    }

    dependencyResolutionManagement {
        repositories {
            mavenLocal()
            google()
            mavenCentral()
            // your repos
        }
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "com.latenighthack.viewmodel") {
                useModule("com.latenighthack.viewmodel:gradle-plugin:${requested.version}")
            }
        }
    }
}
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "viewmodel"
include(":viewmodel-gradle-plugin")
include(":viewmodel-ksp")
include(":viewmodel-compiler-plugin")
include(":viewmodel-lib")
include(":viewmodel-demo")
include(":viewmodel-demo:core")
include(":viewmodel-annotations")
