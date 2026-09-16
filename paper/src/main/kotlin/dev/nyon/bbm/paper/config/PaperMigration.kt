package dev.nyon.bbm.paper.config

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal fun migratePaperConfig(tree: JsonElement, version: Int?): PaperGameplayConfig? = when (version) {
    2 -> migrateVersion2(tree.jsonObject)
    3 -> migrateVersion3(tree.jsonObject)
    4 -> migrateVersion4(tree.jsonObject)
    5 -> migrateVersion5(tree.jsonObject)
    6 -> migrateVersion6(tree.jsonObject)
    else -> null
}

private fun migrateVersion2(json: JsonObject): PaperGameplayConfig? {
    val boostUnderwater = json.boolean("boostUnderwater") ?: return null
    return PaperGameplayConfig(
        playerEjectTicks = json.float("playerEjectTicks") ?: return null,
        boosting = PaperGameplayConfig.Boosting(
            boostStates = mutableSetOf(PaperBoatStatus.ON_LAND, PaperBoatStatus.IN_WATER).also { states ->
                if (boostUnderwater) states.addAll(
                    listOf(PaperBoatStatus.UNDER_WATER, PaperBoatStatus.UNDER_FLOWING_WATER)
                )
            },
            onlyForPlayers = json.boolean("onlyForPlayers") ?: return null
        )
    )
}

private fun migrateVersion3(json: JsonObject): PaperGameplayConfig? = migrateLegacyBoosting(json, false)

private fun migrateVersion4(json: JsonObject): PaperGameplayConfig? = migrateLegacyBoosting(json, false)

private fun migrateVersion5(json: JsonObject): PaperGameplayConfig? = migrateLegacyBoosting(json, true)

private fun migrateVersion6(json: JsonObject): PaperGameplayConfig? = migrateLegacyBoosting(json, true)

private fun migrateLegacyBoosting(json: JsonObject, hasKeybind: Boolean): PaperGameplayConfig? {
    val boostUnderwater = json.boolean("boostUnderwater") ?: return null
    val boostOnBlocks = json.boolean("boostOnBlocks") ?: return null
    val boostOnIce = json.boolean("boostOnIce") ?: return null
    val boostOnWater = json.boolean("boostOnWater") ?: return null
    val keybind = if (hasKeybind) {
        PaperGameplayConfig.Keybind(
            allowJumpKeybind = json.boolean("allowJumpKeybind") ?: return null,
            keybindJumpHeightMultiplier = json.double("keybindJumpHeightMultiplier") ?: return null,
            onlyKeybindJumpOnGroundOrWater = json.boolean("onlyKeybindJumpOnGroundOrWater") ?: return null
        )
    } else PaperGameplayConfig.Keybind()

    return PaperGameplayConfig(
        stepHeight = json.float("stepHeight") ?: return null,
        playerEjectTicks = json.float("playerEjectTicks") ?: return null,
        boosting = PaperGameplayConfig.Boosting(
            boostStates = mutableSetOf<PaperBoatStatus>().also { states ->
                if (boostUnderwater) states.addAll(
                    listOf(PaperBoatStatus.UNDER_WATER, PaperBoatStatus.UNDER_FLOWING_WATER)
                )
                if (boostOnBlocks) states.add(PaperBoatStatus.ON_LAND)
                if (boostOnWater) states.add(PaperBoatStatus.IN_WATER)
            },
            allowedCollidingBlocks = if (boostOnIce) {
                mutableSetOf(PaperIdentifierSerializer.decodeFromString("#minecraft:ice"))
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
