plugins {
    application
    kotlin("jvm") version "1.9.23"
}

group = "me.gabriel.gwydion"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":llvm"))
    implementation(libs.mordant)
}

application {
    mainClass = "me.gabriel.gwydion.Compiler"
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "me.gabriel.gwydion.CompilerKt"
        attributes["Class-Path"] = configurations.runtimeClasspath.get().joinToString(" ") { "libs/$it" }
    }
}