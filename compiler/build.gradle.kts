import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    application
    kotlin("multiplatform")
    alias(libs.plugins.shadow)
}

repositories {
    mavenCentral()
}

kotlin {
    jvm {
       withJava()
    }
    mingwX64("windowsX64") {
        binaries {
            executable()
        }
    }
    linuxX64("linuxX64") {
        binaries {
            executable()
        }
    }
    iosArm64("iosArm64") {
        binaries {
            executable()
        }
    }
    macosX64("macosX64") {
        binaries {
            executable()
        }
    }
    js().browser()
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":analysis"))
                implementation(project(":frontend"))
                implementation(project(":ir"))
                implementation(project(":backend:common"))
                implementation(project(":backend:llvm"))
                implementation(libs.mordant)
                implementation(libs.okio)
                implementation(libs.kotlinx.serialization.json)
            }
        }
        val nativeMain by creating {
            dependsOn(commonMain)
        }
        val linuxX64Main by getting {
            dependsOn(nativeMain)
        }
        val iosArm64Main by getting {
            dependsOn(nativeMain)
        }
        val macosX64Main by getting {
            dependsOn(nativeMain)
        }
        val windowsX64Main by getting {
            dependsOn(nativeMain)
        }
    }
}

tasks.withType<ShadowJar> {
    archiveFileName.set("selene.jar")
}

application {
    mainClass = "me.gabriel.selene.compiler.jvm.JvmLauncherKt"
}