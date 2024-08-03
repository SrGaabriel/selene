plugins {
    kotlin("jvm") version "1.9.23"
}

group = "me.gabriel.gwydion"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.mordant)
}