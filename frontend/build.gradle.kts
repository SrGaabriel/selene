plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    api(project(":tools"))
    compileOnly(libs.kotlinx.serialization.json)
}