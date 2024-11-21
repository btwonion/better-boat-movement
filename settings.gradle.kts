rootProject.name = "better-boat-movement"

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev")
        maven("https://maven.minecraftforge.net")
        maven("https://maven.neoforged.net/releases/")
        maven("https://maven.kikugie.dev/releases")
        maven("https://maven.kikugie.dev/snapshots")
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.5-beta.5"
}

buildscript {
    repositories { mavenCentral() }
    dependencies {
        classpath("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")
    }
}

stonecutter {
    kotlinController = true
    centralScript = "build.gradle.kts"
    shared {
        vers("1.19.4-forge", "1.19.4")
        vers("1.19.4-fabric", "1.19.4")
        vers("1.20.1-forge", "1.20.1")
        vers("1.20.1-fabric", "1.20.1")
        vers("1.20.4-neoforge", "1.20.4")
        vers("1.20.4-fabric", "1.20.4")
        vers("1.20.6-neoforge", "1.20.6")
        vers("1.20.6-fabric", "1.20.6")
        vers("1.21-neoforge", "1.21")
        vers("1.21-fabric", "1.21")
        vers("1.21.3-neoforge", "1.21.3")
        vers("1.21.3-fabric", "1.21.3")
        vcsVersion = "1.21-fabric"
    }
    create(rootProject)
}
