plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.serialization) apply false
}

group = "me.gabriel.selene"
version = "0.0.1-ALPHA"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
}

allprojects {
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
}