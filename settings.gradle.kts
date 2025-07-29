rootProject.name = "better-boat-movement"

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev")
        maven("https://maven.minecraftforge.net")
        maven("https://maven.neoforged.net/releases/")
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.7"
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
        vers("1.21-neoforge", "1.21")
        vers("1.21-fabric", "1.21")
        vers("1.21.3-neoforge", "1.21.3")
        vers("1.21.3-fabric", "1.21.3")
        vers("1.21.7-neoforge", "1.21.7")
        vcsVersion = "1.21.3-fabric"
    }
    create(rootProject)
}
