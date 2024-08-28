plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project(":frontend"))
    compileOnly(libs.kotlinx.serialization.json)
}