@file:Suppress("SpellCheckingInspection", "UnstableApiUsage", "RedundantNullableReturnType")

import net.fabricmc.loom.util.ModPlatform
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.architectury.loom)
    alias(libs.plugins.mod.publish)

    `maven-publish`
}

val loader = loom.platform.get()
val isFabric = loader == ModPlatform.FABRIC

val beta: Int = property("mod.beta").toString().toInt()
val majorVersion: String = property("mod.major-version").toString()
val mcVersion = property("vers.mcVersion").toString() // Pattern is '1.0.0-beta1-1.20.6-pre.2+fabric'
version = "$majorVersion${if (beta != 0) "-beta$beta" else ""}-$mcVersion+${loader.name.lowercase()}"

group = property("mod.group").toString()
val githubRepo = property("mod.repo").toString()

base {
    archivesName.set(rootProject.name)
}

val mixinsFile = property("mod.mixins").toString()
loom {
    if (stonecutter.current.isActive) {
        runConfigs.all {
            ideConfigGenerated(true)
            runDir("../../run")
        }
    }

    if (loader == ModPlatform.FORGE) forge {
        mixinConfigs(mixinsFile)
    }

    mixin { useLegacyMixinAp = false }
    silentMojangMappingsLicense()
}

repositories {
    mavenCentral()
    maven("https://maven.terraformersmc.com")
    maven("https://maven.quiltmc.org/repository/release/")
    maven("https://repo.nyon.dev/releases")
    maven("https://maven.isxander.dev/releases")
    maven("https://thedarkcolour.github.io/KotlinForForge/")
    maven("https://maven.neoforged.net/releases/")
}

val flk: String = "${libs.versions.fabric.language.kotlin.orNull}${libs.versions.kotlin.orNull}"
val fapi: String by lazy { property("vers.deps.fapi").toString() }
val yacl: String by lazy { property("vers.deps.yacl").toString() }
val modmenu: String by lazy { property("vers.deps.modMenu").toString() }
dependencies {
    minecraft("com.mojang:minecraft:$mcVersion")
    mappings(loom.layered {
        val quiltMappings: String = property("vers.deps.quiltmappings").toString()
        if (quiltMappings.isNotEmpty()) mappings("org.quiltmc:quilt-mappings:$quiltMappings:intermediary-v2")
        officialMojangMappings()
    })

    implementation(libs.vineflower)

    if (isFabric) {
        implementation(libs.fabric.loader)
        modImplementation("net.fabricmc.fabric-api:fabric-api:$fapi")
        modImplementation("net.fabricmc:fabric-language-kotlin:$flk")
        modImplementation("com.terraformersmc:modmenu:$modmenu")
        modCompileOnly("dev.isxander:yet-another-config-lib:$yacl")
    } else {
        if (loader == ModPlatform.FORGE) {
            "forge"("net.minecraftforge:forge:$mcVersion-${property("vers.deps.fml")}")
            compileOnly(libs.mixinextras.common)
            annotationProcessor(libs.mixinextras.common)
            include(libs.mixinextras.forge)
            implementation(libs.mixinextras.forge)
        } else
            "neoForge"("net.neoforged:neoforge:${property("vers.deps.fml")}")
        modImplementation("dev.nyon:KotlinLangForge:1.2.0-k${libs.versions.kotlin.orNull}-$mcVersion+${loader.name.lowercase()}")
    }

    modImplementation(libs.konfig)
    include(libs.konfig)
}

val javaVersion = if (stonecutter.eval(mcVersion, ">=1.20.6")) 21 else 17
val modId = property("mod.id").toString()
val modName = property("mod.name").toString()
val modDescription = property("mod.description").toString()
val mcVersionRange = property("vers.mcVersionRange").toString()
val icon = property("mod.icon").toString()
val slug = property("mod.slug").toString()
tasks {
    processResources {
        val props: Map<String, String?> = mapOf(
            "id" to modId,
            "name" to modName,
            "description" to modDescription,
            "version" to project.version.toString(),
            "github" to githubRepo,
            "mc" to mcVersionRange,
            "flk" to if (!isFabric) null else flk,
            "fapi" to if (!isFabric) null else fapi,
            "yacl" to if (!isFabric) null else yacl,
            "modmenu" to if (!isFabric) null else modmenu,
            "repo" to githubRepo,
            "icon" to icon,
            "mixins" to mixinsFile,
            "slug" to slug
        ).filterNot { it.value == null }

        props.forEach(inputs::property)

        if (isFabric) {
            filesMatching("fabric.mod.json") { expand(props) }
            exclude(listOf("META-INF/mods.toml", "META-INF/neoforge.mods.toml", "pack.mcmeta"))
        } else {
            filesMatching(listOf("META-INF/mods.toml", "META-INF/neoforge.mods.toml")) { expand(props) }
            exclude("fabric.mod.json")
        }
    }

    register("releaseMod") {
        group = "publishing"

        dependsOn("publishMods")
        dependsOn("publish")
    }

    withType<JavaCompile> {
        options.release = javaVersion
    }

    withType<KotlinCompile> {
        compilerOptions {
            jvmTarget = JvmTarget.fromTarget(javaVersion.toString())
        }
    }
}

val changelogText = buildString {
    append("# v${project.version}\n")
    if (beta != 0) appendLine("### As this is still a beta version, this version can contain bugs. Feel free to report ANY misbehaviours and errors!")
    rootDir.resolve("changelog.md").readText().also(::append)
}

val supportedMcVersions: List<String> =
    property("vers.supportedMcVersions")!!.toString().split(',').map(String::trim).filter(String::isNotEmpty)

publishMods {
    displayName = "v${project.version}"
    file = tasks.remapJar.get().archiveFile
    changelog = changelogText
    type = if (beta != 0) BETA else STABLE
    when (loader) {
        ModPlatform.FABRIC -> modLoaders.addAll("fabric", "quilt")
        ModPlatform.FORGE -> modLoaders.addAll("forge")
        ModPlatform.NEOFORGE -> modLoaders.addAll("neoforge")
        else -> {}
    }

    modrinth {
        projectId = "wTfH1dkt"
        accessToken = providers.environmentVariable("MODRINTH_API_KEY")
        minecraftVersions.addAll(supportedMcVersions)

        if (isFabric) {
            requires { slug = "fabric-api" }
            requires { slug = "fabric-language-kotlin" }
            optional { slug = "modmenu" }
            if (stonecutter.compare(mcVersion, "1.20.1") >= 0) requires { slug = "yacl" }
        } else {
            requires { slug = "kotlin-lang-forge" }
        }
    }

    github {
        repository = githubRepo
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
            groupId = "dev.nyon"
            artifactId = modName
            version = project.version.toString()
            from(components["java"])
        }
    }
}

java {
    withSourcesJar()

    javaVersion.toInt().let { JavaVersion.values()[it - 1] }.let {
        sourceCompatibility = it
        targetCompatibility = it
    }
}
