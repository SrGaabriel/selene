plugins {
    kotlin("multiplatform")
}

repositories {
    mavenCentral()
}

kotlin {
    jvm()
    iosArm64()
    macosX64()
    js().browser()
    sourceSets {
        val commonMain by getting {
            dependencies {
                compileOnly(project(":frontend"))
                compileOnly(libs.kotlinx.serialization.json)
            }
        }
    }
}