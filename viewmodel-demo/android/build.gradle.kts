import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.kotlin.reflect)
            implementation(libs.ktstore.library)
            implementation(libs.viewmodel.library)
            implementation(libs.androidx.recyclerview)
            implementation(libs.coroutines.android)
        }
        commonMain.dependencies {
            implementation(libs.ktstore.library)
            implementation(projects.core)
        }
    }
}

android {
    namespace = "com.latenighthack"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.latenighthack"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    packagingOptions {
        merge("META-INF/versions/9/OSGI-INF/MANIFEST.MF")
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    dependencies {
    }
}
