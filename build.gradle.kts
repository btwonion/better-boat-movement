plugins {
    base
}

val modTargets = project(":mod").subprojects

tasks {
    named("build") {
        description = "Build every mod and plugin target"
        dependsOn(modTargets.map { "${it.path}:build" })
        dependsOn(":paper:build")
    }

    register("releaseAllPlatforms") {
        group = "publishing"
        description = "Release every mod and plugin target"
        dependsOn(modTargets.map { "${it.path}:releaseMod" })
        dependsOn(":paper:releasePlugin")
    }

    register("postUpdate") {
        group = "publishing"
        dependsOn(":mod:postUpdate")
    }
}
