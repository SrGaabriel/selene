plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project(":analysis"))
    compileOnly(project(":frontend"))
    implementation(project(":llvm"))
}