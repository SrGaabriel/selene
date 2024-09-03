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
    mingwX64("native") {
        binaries {
            executable()
        }
    }
    linuxX64()
    iosArm64()
    macosX64()
    js().browser()
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":analysis"))
                implementation(project(":frontend"))
                implementation(project(":ir"))
                implementation(libs.mordant)
                implementation(libs.okio)
                implementation(libs.kotlinx.serialization.json)
            }
        }
    }
}

tasks.withType<ShadowJar> {
    archiveFileName.set("gwydion.jar")
}

application {
    mainClass = "me.gabriel.gwydion.compiler.jvm.JvmLauncherKt"
}