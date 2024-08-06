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
                useModule("com.latenighthack.viewmodel:gradle-plugin:0.0.4-2.0.0")//${requested.version}")
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
include(":viewmodel-annotations")
//includeBuild("viewmodel-demo")
