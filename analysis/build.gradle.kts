plugins {
    kotlin("multiplatform")
}

repositories {
    mavenCentral()
}

kotlin {
    jvm()
    mingwX64()
    iosArm64()
    linuxX64()
    macosX64()
    js().browser()
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":frontend"))
                implementation(libs.kotlinx.serialization.core)
            }
        }
    }
}