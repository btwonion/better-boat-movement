@file:Suppress("SpellCheckingInspection")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.24"
    kotlin("plugin.serialization") version "1.9.24"
    id("fabric-loom") version "1.6-SNAPSHOT"

    id("me.modmuss50.mod-publish-plugin") version "0.5.+"

    `maven-publish`
    signing
}

val featureVersion = "1.1.3"
val mcVersion = property("mcVersion")!!.toString()
val currentVersion = stonecutter.current.version
val mcVersionRange = property("mcVersionRange")!!.toString()
version = "$featureVersion-$mcVersion"

group = "dev.nyon"
val authors = listOf("btwonion")
val githubRepo = "btwonion/better-boat-movement"

loom {
    if (stonecutter.current.isActive) {
        runConfigs.all {
            ideConfigGenerated(true)
            runDir("../../run")
        }
    }

    mixin { useLegacyMixinAp = false }
}

repositories {
    mavenCentral()
    maven("https://maven.terraformersmc.com")
    maven("https://maven.parchmentmc.org")
    maven("https://repo.nyon.dev/releases")
    maven("https://maven.isxander.dev/releases")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    minecraft("com.mojang:minecraft:$mcVersion")
    mappings(
        loom.layered {
            parchment("org.parchmentmc.data:parchment-${property("deps.parchment")}@zip")
            officialMojangMappings()
        }
    )

    implementation("org.vineflower:vineflower:1.10.1")
    modImplementation("net.fabricmc:fabric-loader:0.15.11")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fapi")!!}")
    modImplementation("net.fabricmc:fabric-language-kotlin:1.10.20+kotlin.1.9.24")

    modImplementation("dev.isxander:yet-another-config-lib:${property("deps.yacl")!!}")
    modImplementation("com.terraformersmc:modmenu:${property("deps.modMenu")!!}")

    include(modImplementation("dev.nyon:konfig:2.0.1-1.20.4")!!)
}

val javaVersion = property("javaVer")!!.toString()
tasks {
    processResources {
        val modId = "better-boat-movement"
        val modName = "BetterBoatMovement"
        val modDescription = "Increases boat step height to move up water and blocks"

        val props =
            mapOf(
                "id" to modId,
                "name" to modName,
                "description" to modDescription,
                "version" to currentVersion,
                "github" to githubRepo,
                "mc" to mcVersionRange
            )

        props.forEach(inputs::property)

        filesMatching("fabric.mod.json") {
            expand(props)
        }
    }

    register("releaseMod") {
        group = "publishing"

        dependsOn("publishMods")
        dependsOn("publish")
    }

    withType<JavaCompile> {
        options.release = javaVersion.toInt()
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = javaVersion
    }
}

val changelogText =
    buildString {
        append("# v${project.version}\n")
        file("../../changelog.md").readText().also { append(it) }
    }

val supportedMcVersions: List<String> = property("supportedMcVersions")!!.toString().split(',').map(String::trim).filter(String::isNotEmpty)

publishMods {
    displayName = "v${project.version}"
    file = tasks.remapJar.get().archiveFile
    changelog = changelogText
    type = STABLE
    modLoaders.addAll("fabric", "quilt")

    modrinth {
        projectId = "wTfH1dkt"
        accessToken = providers.environmentVariable("MODRINTH_API_KEY")
        minecraftVersions.addAll(supportedMcVersions)

        requires { slug = "fabric-api" }
        requires { slug = "yacl" }
        requires { slug  = "fabric-language-kotlin" }
        optional { slug = "modmenu" }
    }

    github {
        repository = githubRepo
        accessToken = providers.environmentVariable("GITHUB_TOKEN")
        commitish = "master"
    }

    discord {
        webhookUrl = providers.environmentVariable("DISCORD_WEBHOOK")
        username = "Release Notifier"
        avatarUrl = "https://www.svgrepo.com/show/521999/bell.svg"
    }
}

publishing {
    repositories {
        maven {
            name = "nyon"
            url = uri("https://repo.nyon.dev/releases")
            credentials {
                username = providers.environmentVariable("NYON_USERNAME").toString()
                password = providers.environmentVariable("NYON_PASSWORD").toString()
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "dev.nyon"
            artifactId = "better-boat-movement"
            version = project.version.toString()
            from(components["java"])
        }
    }
}

java {
    withSourcesJar()

    javaVersion.toInt()
        .let { JavaVersion.values()[it - 1] }
        .let {
            sourceCompatibility = it
            targetCompatibility = it
        }
}

kotlin {
    jvmToolchain(javaVersion.toInt())
}

signing {
    sign(publishing.publications)
}
