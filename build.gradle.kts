import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    application
    kotlin("jvm") version "1.9.23"
    id("com.github.johnrengelman.shadow") version "8.1.1"
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
    mainClass = "me.gabriel.gwydion.CompilerKt"
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "me.gabriel.gwydion.CompilerKt"
        attributes["Class-Path"] = configurations.runtimeClasspath.get().joinToString(" ") { "libs/$it" }
    }
}

tasks.withType<ShadowJar> {
    archiveFileName.set("gwydion.jar")
}