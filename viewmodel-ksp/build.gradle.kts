import com.google.protobuf.gradle.remove
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

val enableSigning = project.hasProperty("signingInMemoryKey")

plugins {
    kotlin("jvm")
    id("com.vanniktech.maven.publish")
    `maven-publish`
    signing
    kotlin("kapt")
    id("app.cash.licensee")
    id("com.google.protobuf") version "0.9.4"
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.20.1"
    }

    generateProtoTasks {
        all().forEach {
            it.builtins {
                remove("java")
            }

            it.plugins {
                create("kt") {
                    outputSubDir = "kotlin"
                }
            }
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(7))
        version = 17
    }
}

licensee {
    allow("Apache-2.0")
    allow("MIT")
}

kotlin {
    jvmToolchain(17)
}

mavenPublishing {
    coordinates(
        libs.versions.groupId.get(),
        "viewmodel-ksp",
        libs.versions.viewmodelKsp.get(),
    )
    // publishToMavenCentral(SonatypeHost.S01) for publishing through s01.oss.sonatype.org
    if (enableSigning) {
        signAllPublications()
    }
}

dependencies {
//    implementation(projects.viewmodelAnnotations)
    implementation(project(":viewmodel-annotations"))
    implementation(libs.kspApi)
    implementation(libs.kotlinPoet)
    implementation(libs.kotlinPoet.ksp)
    implementation(libs.ktbuf)

    compileOnly(libs.autoService)
    kapt(libs.autoService)

    // TEST
    testImplementation(libs.junit)
    testImplementation(libs.kctfork.core)
    testImplementation(libs.kctfork.ksp)
    testImplementation(libs.mockito.kotlin)
}

publishing {
    publications {
        create<MavenPublication>("default") {
            from(components["java"])

            pom {
                name.set(project.name)
                description.set("KSP Plugin for ViewModel")
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

tasks.withType<KotlinCompilationTask<*>>().configureEach {
    compilerOptions.freeCompilerArgs.add("-opt-in=org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi")
}
