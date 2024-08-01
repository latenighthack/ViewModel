import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


val enableSigning = project.hasProperty("signingInMemoryKey")

plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("com.vanniktech.maven.publish")
    `maven-publish`
    signing
    alias(libs.plugins.detekt)
    id("app.cash.licensee")
    id("org.jlleitschuh.gradle.ktlint")
}

licensee {
    allow("Apache-2.0")
    allow("MIT")
}

mavenPublishing {
    coordinates(libs.versions.groupId.get(), "compiler-plugin", libs.versions.viewmodelCompiler.get())
    // publishToMavenCentral(SonatypeHost.S01) for publishing through s01.oss.sonatype.org
    if (enableSigning) {
        signAllPublications()
    }
}

dependencies {
    compileOnly(libs.autoService)
    kapt(libs.autoService)
    compileOnly(libs.kotlin.compiler.embeddable)
    testImplementation(libs.kctfork.core)
    testImplementation(libs.junit)
    testImplementation(kotlin("reflect"))
}

publishing {
    publications {
        create<MavenPublication>("default") {
            from(components["java"])

            pom {
                name.set("compiler-plugin")
                description.set("Compiler Plugin for ViewModel")
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

//java {
//    toolchain {
//        languageVersion.set(JavaLanguageVersion.of(8))
//    }
//}
