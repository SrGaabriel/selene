plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(project(":analysis"))
    compileOnly(project(":frontend"))
    compileOnly(project(":llvm"))
}