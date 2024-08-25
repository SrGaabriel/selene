import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    application
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization) apply false
    alias(libs.plugins.shadow)
}

group = "me.gabriel.gwydion"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

allprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
}

dependencies {
    implementation(project(":llvm"))
    implementation(libs.mordant)
    implementation(libs.kotlinx.serialization.json)
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