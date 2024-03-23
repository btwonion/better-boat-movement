@file:Suppress("SpellCheckingInspection")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.serialization") version "1.9.23"
    id("fabric-loom") version "1.6-SNAPSHOT"

    id("com.modrinth.minotaur") version "2.8.7"
    id("com.github.breadmoirai.github-release") version "2.5.2"

    `maven-publish`
    signing
}

group = "dev.nyon"
val majorVersion = "1.0.0"
val mcVersion = "1.20.1"
version = "$majorVersion-$mcVersion"
val authors = listOf("btwonion")
val githubRepo = "btwonion/better-boat-movement"

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
    mappings(loom.layered {
        parchment("org.parchmentmc.data:parchment-1.20.1:2023.09.03@zip")
        officialMojangMappings()
    })

    implementation("org.vineflower:vineflower:1.9.3")
    modImplementation("net.fabricmc:fabric-loader:0.15.7")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.92.0+$mcVersion")
    modImplementation("net.fabricmc:fabric-language-kotlin:1.10.19+kotlin.1.9.23")

    modImplementation("dev.isxander.yacl:yet-another-config-lib-fabric:3.2.2+1.20")
    modImplementation("com.terraformersmc:modmenu:7.2.2")

    include(modImplementation("dev.nyon:konfig:1.1.0-1.20.4")!!)
}

tasks {
    processResources {
        val modId = "better-boat-movement"
        val modName = "BetterBoatMovement"
        val modDescription = "Increases boat step height to move up water and blocks"

        inputs.property("id", modId)
        inputs.property("name", modName)
        inputs.property("description", modDescription)
        inputs.property("version", project.version)
        inputs.property("github", githubRepo)

        filesMatching("fabric.mod.json") {
            expand(
                "id" to modId,
                "name" to modName,
                "description" to modDescription,
                "version" to project.version,
                "github" to githubRepo
            )
        }
    }

    register("releaseMod") {
        group = "publishing"

        dependsOn("modrinthSyncBody")
        dependsOn("modrinth")
        dependsOn("githubRelease")
        dependsOn("publish")
    }

    withType<JavaCompile> {
        options.release.set(17)
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }
}

val changelogText =
    buildString {
        append("# v${project.version}\n")
        file("changelog.md").readText().also { append(it) }
    }

modrinth {
    token.set(findProperty("modrinth.token")?.toString())
    projectId.set("")
    versionNumber.set("${project.version}")
    versionType.set("release")
    uploadFile.set(tasks["remapJar"])
    gameVersions.set(listOf(mcVersion))
    loaders.set(listOf("fabric", "quilt"))
    dependencies {
        required.project("fabric-api")
        required.project("fabric-language-kotlin")
        required.project("yacl")
        optional.project("modmenu")
    }
    changelog.set(changelogText)
    syncBodyFrom.set(file("readme.md").readText())
}

githubRelease {
    token(findProperty("github.token")?.toString())

    val split = githubRepo.split("/")
    owner = split[0]
    repo = split[1]
    tagName = "v${project.version}"
    body = changelogText
    overwrite = true
    releaseAssets(tasks["remapJar"].outputs.files)
    targetCommitish = "main"
}

publishing {
    repositories {
        maven {
            name = "nyon"
            url = uri("https://repo.nyon.dev/releases")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
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
}

signing {
    sign(publishing.publications)
}
