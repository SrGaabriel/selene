import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    application
    kotlin("jvm")
    alias(libs.plugins.shadow)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":frontend"))
    implementation(project(":analysis"))
    implementation(project(":ir"))
    implementation(libs.mordant)
    implementation(libs.kotlinx.serialization.json)
}

application {
    mainClass = "me.gabriel.gwydion.compiler.CompilerKt"
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "me.gabriel.gwydion.compiler.CompilerKt"
        attributes["Class-Path"] = configurations.runtimeClasspath.get().joinToString(" ") { "libs/$it" }
    }
}

tasks.withType<ShadowJar> {
    archiveFileName.set("gwydion.jar")
}