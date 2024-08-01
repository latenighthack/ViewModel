import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("java-gradle-plugin")
    `maven-publish`
    `kotlin-dsl`
    id("com.gradle.plugin-publish") version "1.2.1"
    id("com.vanniktech.maven.publish")
    id("org.jlleitschuh.gradle.ktlint")
}

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://maven.google.com")
        maven("https://plugins.gradle.org/m2/")
        google()
    }
}

dependencies {
    add("compileOnly", kotlin("gradle-plugin"))
}

dependencies {
    implementation(libs.kotlin.gradle.plugin.api)
}

gradlePlugin {
    website.set("https://github.com/latenighthack/viewmodel")
    vcsUrl.set("https://github.com/latenighthack/viewmodel")
    plugins {

        create("viewmodelPlugin") {
            id = "com.latenighthack.viewmodel" // users will do `apply plugin: "com.latenighthack.viewmodel"`
            implementationClass = "com.latenighthack.viewmodel.ViewModelGradlePlugin" // entry-point class
            displayName = "ViewModel Gradle Plugin"
            description = "Gradle Plugin for ViewModel"
            tags.set(listOf("kotlin", "kotlin-mpp"))
        }
    }
}

val enableSigning = project.hasProperty("signingInMemoryKey")

mavenPublishing {
    coordinates(
        libs.versions.groupId.get(),
        "viewmodel-gradle-plugin",
        "${libs.versions.viewmodelGradle.get()}-${libs.versions.kotlin.get()}"
    )
    // publishToMavenCentral(SonatypeHost.S01) for publishing through s01.oss.sonatype.org
    if (enableSigning) {
        signAllPublications()
    }
}

publishing {
    publications {
        create<MavenPublication>("default") {
            from(components["java"])

            pom {
                name.set("viewmodel-gradle-plugin")
                description.set("Gradle plugin for ViewModel")
                url.set("https://github.com/latenighthack/viewmodel")

                licenses {
                    license {
                        name.set("Apache License 2.0")
                        url.set("https://github.com/latenighthack/viewmodel/blob/master/LICENSE.txt")
                    }
                }
                scm {
                    url.set("https://github.com/latenighthack/viewmodel")
                    connection.set("scm:git:git://github.com/latenighthack/viewmodel.git")
                }
                developers {
                    developer {
                        name.set("Mike Roberts")
                        url.set("https://github.com/mproberts")
                    }
                }
            }
        }
    }

    repositories {
        if (
            hasProperty("sonatypeUsername") &&
            hasProperty("sonatypePassword") &&
            hasProperty("sonatypeSnapshotUrl") &&
            hasProperty("sonatypeReleaseUrl")
        ) {
            maven {
                val url =
                    when {
                        "SNAPSHOT" in version.toString() -> property("sonatypeSnapshotUrl")
                        else -> property("sonatypeReleaseUrl")
                    } as String
                setUrl(url)
                credentials {
                    username = property("sonatypeUsername") as String
                    password = property("sonatypePassword") as String
                }
            }
        }
    }
}
