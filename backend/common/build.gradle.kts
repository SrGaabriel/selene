plugins {
    kotlin("multiplatform")
}

repositories {
    mavenCentral()
}

kotlin {
    jvm()
    mingwX64()
    linuxX64()
    iosArm64()
    macosX64()
    js().browser()

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":analysis"))
                api(project(":frontend"))
            }
        }
    }
}