package dev.nyon.bbm.config

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import net.minecraft.world.entity.vehicle.boat.AbstractBoat.Status

internal fun migrateConfig(tree: JsonElement, version: Int?): GameplayConfig? = when (version) {
    2 -> migrateVersion2(tree.jsonObject)
    3 -> migrateVersion3(tree.jsonObject)
    4 -> migrateVersion4(tree.jsonObject)
    5 -> migrateVersion5(tree.jsonObject)
    6 -> migrateVersion6(tree.jsonObject)
    else -> null
}

private fun migrateVersion2(json: JsonObject): GameplayConfig? {
    val boostUnderwater = json.boolean("boostUnderwater") ?: return null
    return GameplayConfig(
        playerEjectTicks = json.float("playerEjectTicks") ?: return null,
        boosting = GameplayConfig.Boosting(
            boostStates = mutableSetOf(Status.ON_LAND, Status.IN_WATER).also { states ->
                if (boostUnderwater) states.addAll(listOf(Status.UNDER_WATER, Status.UNDER_FLOWING_WATER))
            },
            onlyForPlayers = json.boolean("onlyForPlayers") ?: return null
        )
    )
}

private fun migrateVersion3(json: JsonObject): GameplayConfig? = migrateLegacyBoosting(json, hasKeybind = false)

private fun migrateVersion4(json: JsonObject): GameplayConfig? = migrateLegacyBoosting(json, hasKeybind = false)

private fun migrateVersion5(json: JsonObject): GameplayConfig? = migrateLegacyBoosting(json, hasKeybind = true)

private fun migrateVersion6(json: JsonObject): GameplayConfig? = migrateLegacyBoosting(json, hasKeybind = true)

private fun migrateLegacyBoosting(json: JsonObject, hasKeybind: Boolean): GameplayConfig? {
    val boostUnderwater = json.boolean("boostUnderwater") ?: return null
    val boostOnBlocks = json.boolean("boostOnBlocks") ?: return null
    val boostOnIce = json.boolean("boostOnIce") ?: return null
    val boostOnWater = json.boolean("boostOnWater") ?: return null
    val keybind = if (hasKeybind) {
        GameplayConfig.Keybind(
            allowJumpKeybind = json.boolean("allowJumpKeybind") ?: return null,
            keybindJumpHeightMultiplier = json.double("keybindJumpHeightMultiplier") ?: return null,
            onlyKeybindJumpOnGroundOrWater = json.boolean("onlyKeybindJumpOnGroundOrWater") ?: return null
        )
    } else GameplayConfig.Keybind()

    return GameplayConfig(
        stepHeight = json.float("stepHeight") ?: return null,
        playerEjectTicks = json.float("playerEjectTicks") ?: return null,
        boosting = GameplayConfig.Boosting(
            boostStates = mutableSetOf<Status>().also { states ->
                if (boostUnderwater) states.addAll(listOf(Status.UNDER_WATER, Status.UNDER_FLOWING_WATER))
                if (boostOnBlocks) states.add(Status.ON_LAND)
                if (boostOnWater) states.add(Status.IN_WATER)
            },
            allowedCollidingBlocks = if (boostOnIce) {
                mutableSetOf(IdentifierSerializer.decodeFromString("#minecraft:ice"))
            } else mutableSetOf(),
            onlyForPlayers = json.boolean("onlyForPlayers") ?: return null,
            extraCollisionDetectionRange = json.double("extraCollisionDetectionRange") ?: return null
        ),
        keybind = keybind
    )
}

private fun JsonObject.boolean(name: String) = this[name]?.jsonPrimitive?.booleanOrNull
private fun JsonObject.float(name: String) = this[name]?.jsonPrimitive?.floatOrNull
private fun JsonObject.double(name: String) = this[name]?.jsonPrimitive?.doubleOrNull
