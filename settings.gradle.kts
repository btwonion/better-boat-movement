rootProject.name = "better-boat-movement"

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev")
        maven("https://maven.neoforged.net/releases/")
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.7.10"
}

buildscript {
    repositories { mavenCentral() }
    dependencies {
        classpath("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
    }
}

stonecutter {
    kotlinController = true
    centralScript = "build.gradle.kts"
    shared {
        version("1.21-neoforge", "1.21")
        version("1.21-fabric", "1.21")
        version("1.21.3-neoforge", "1.21.3")
        version("1.21.3-fabric", "1.21.3")
        version("1.21.7-neoforge", "1.21.7")
        version("1.21.9-neoforge", "1.21.9")
        version("1.21.9-fabric", "1.21.9")
        version("1.21.11-neoforge", "1.21.11")
        version("1.21.11-fabric", "1.21.11")
        vcsVersion = "1.21.11-fabric"
    }
    create(rootProject)
}
