@file:Suppress("SpellCheckingInspection", "UnstableApiUsage")

import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import xyz.jpenilla.resourcefactory.paper.PaperPluginYaml

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.mod.publish)
    alias(libs.plugins.paper.userdev)
    alias(libs.plugins.run.paper)
    alias(libs.plugins.rfpc)

    `maven-publish`
}

val beta = property("mod.beta").toString().toInt()
val featureVersion = "${property("mod.major-version")}${if (beta != 0) "-beta$beta" else ""}"
val minecraftVersion = property("paper.minecraft-version").toString()
val minecraftVersionName = property("paper.version-name").toString()
version = "$featureVersion-$minecraftVersionName+paper"
group = property("mod.group").toString()

base {
    archivesName.set(rootProject.name)
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.nyon.dev/releases")
}

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

dependencies {
    paperweight.paperDevBundle(minecraftVersion)

    implementation(libs.konfig)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.10.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.10.0")

    testImplementation(platform("org.junit:junit-bom:5.13.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

paperPluginYaml {
    name = "BetterBoatMovement"
    main = "dev.nyon.bbm.paper.PaperEntrypoint"
    loader = "dev.nyon.bbm.paper.PaperLoader"
    foliaSupported = true
    apiVersion = "1.21"
}

tasks {
    register("releasePlugin") {
        group = "publishing"
        description = "Publish the Paper plugin to Modrinth, GitHub and Maven"
        dependsOn("publishMods")
        dependsOn("publish")
    }

    withType<KotlinCompile> {
        compilerOptions.jvmTarget = JvmTarget.JVM_25
    }

    test {
        useJUnitPlatform()
    }
}

runPaper.folia.registerTask()

val changelogText = buildString {
    append("# v${project.version}\n")
    rootProject.file("changelog.md").readText().also(::append)
}

val supportedMinecraftVersions = property("paper.supported-minecraft-versions").toString()
    .split(',').map(String::trim).filter(String::isNotEmpty)

publishMods {
    displayName = "v${project.version}"
    changelog = changelogText
    file = tasks.jar.get().archiveFile
    type = if (beta != 0) BETA else STABLE
    modLoaders.addAll("paper", "folia", "purpur")

    modrinth {
        projectId = "wTfH1dkt"
        accessToken = providers.environmentVariable("MODRINTH_API_KEY")
        minecraftVersions.addAll(supportedMinecraftVersions)
    }

    github {
        repository = property("mod.repo").toString()
        accessToken = providers.environmentVariable("GITHUB_TOKEN")
        commitish = property("mod.main-branch").toString()
    }
}

publishing {
    repositories {
        maven {
            name = "nyon"
            url = uri("https://repo.nyon.dev/releases")
            credentials {
                username = providers.environmentVariable("NYON_USERNAME").orNull
                password = providers.environmentVariable("NYON_PASSWORD").orNull
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = property("mod.group").toString()
            artifactId = property("mod.name").toString()
            version = project.version.toString()
            from(components["java"])
        }
    }
}

java {
    withSourcesJar()
    JavaVersion.VERSION_25.let {
        sourceCompatibility = it
        targetCompatibility = it
    }
}
