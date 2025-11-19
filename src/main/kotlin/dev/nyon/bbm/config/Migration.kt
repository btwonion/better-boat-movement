package dev.nyon.bbm.config

import dev.nyon.bbm.extensions.Status
import dev.nyon.bbm.extensions.resourceLocation
import kotlinx.serialization.json.*

internal fun migrateConfig(tree: JsonElement, version: Int?): Config? {
    val jsonObject = tree.jsonObject
    return when (version) {
        1 -> null
        2 -> {
            val boostUnderwater = jsonObject["boostUnderwater"]?.jsonPrimitive?.booleanOrNull ?: return null
            Config(
                playerEjectTicks = jsonObject["playerEjectTicks"]?.jsonPrimitive?.floatOrNull ?: return null,
                boosting = Config.Boosting(
                    boostStates = mutableSetOf(Status.ON_LAND, Status.IN_WATER).also {
                        if (boostUnderwater) it.addAll(
                            listOf(Status.UNDER_WATER, Status.UNDER_FLOWING_WATER)
                        )
                    }, onlyForPlayers = jsonObject["onlyForPlayers"]?.jsonPrimitive?.booleanOrNull ?: return null
                ),
            )
        }
        3, 4 -> {
            val boostUnderwater = jsonObject["boostUnderwater"]?.jsonPrimitive?.booleanOrNull ?: return null
            val boostOnBlocks = jsonObject["boostOnBlocks"]?.jsonPrimitive?.booleanOrNull ?: return null
            val boostOnIce = jsonObject["boostOnIce"]?.jsonPrimitive?.booleanOrNull ?: return null
            val boostOnWater = jsonObject["boostOnWater"]?.jsonPrimitive?.booleanOrNull ?: return null

            Config(
                jsonObject["stepHeight"]?.jsonPrimitive?.floatOrNull ?: return null,
                jsonObject["playerEjectTicks"]?.jsonPrimitive?.floatOrNull ?: return null,
                boosting = Config.Boosting(
                    boostStates = mutableSetOf<Status>().also {
                        if (boostUnderwater) it.addAll(listOf(Status.UNDER_WATER, Status.UNDER_FLOWING_WATER))
                        if (boostOnBlocks) it.add(Status.ON_LAND)
                        if (boostOnWater) it.add(Status.IN_WATER)
                    },
                    allowedCollidingBlocks = mutableSetOf<Identifier>().also {
                        if (boostOnIce) it.add(
                            Identifier(
                                resourceLocation("#ice") ?: return null, true
                            )
                        )
                    },
                    onlyForPlayers = jsonObject["onlyForPlayers"]?.jsonPrimitive?.booleanOrNull ?: return null,
                    extraCollisionDetectionRange = jsonObject["extraCollisionDetectionRange"]?.jsonPrimitive?.doubleOrNull
                        ?: return null
                )
            )
        }
        5, 6 -> {
            val boostUnderwater = jsonObject["boostUnderwater"]?.jsonPrimitive?.booleanOrNull ?: return null
            val boostOnBlocks = jsonObject["boostOnBlocks"]?.jsonPrimitive?.booleanOrNull ?: return null
            val boostOnIce = jsonObject["boostOnIce"]?.jsonPrimitive?.booleanOrNull ?: return null
            val boostOnWater = jsonObject["boostOnWater"]?.jsonPrimitive?.booleanOrNull ?: return null

            Config(
                jsonObject["stepHeight"]?.jsonPrimitive?.floatOrNull ?: return null,
                jsonObject["playerEjectTicks"]?.jsonPrimitive?.floatOrNull ?: return null,
                boosting = Config.Boosting(
                    boostStates = mutableSetOf<Status>().also {
                        if (boostUnderwater) it.addAll(listOf(Status.UNDER_WATER, Status.UNDER_FLOWING_WATER))
                        if (boostOnBlocks) it.add(Status.ON_LAND)
                        if (boostOnWater) it.add(Status.IN_WATER)
                    },
                    allowedCollidingBlocks = mutableSetOf<Identifier>().also {
                        if (boostOnIce) it.add(
                            Identifier(
                                resourceLocation("#ice") ?: return null, true
                            )
                        )
                    },
                    onlyForPlayers = jsonObject["onlyForPlayers"]?.jsonPrimitive?.booleanOrNull ?: return null,
                    extraCollisionDetectionRange = jsonObject["extraCollisionDetectionRange"]?.jsonPrimitive?.doubleOrNull
                        ?: return null
                ),
                keybind = Config.Keybind(
                    allowJumpKeybind = jsonObject["allowJumpKeybind"]?.jsonPrimitive?.booleanOrNull ?: return null,
                    keybindJumpHeightMultiplier = jsonObject["keybindJumpHeightMultiplier"]?.jsonPrimitive?.doubleOrNull
                        ?: return null,
                    onlyKeybindJumpOnGroundOrWater = jsonObject["onlyKeybindJumpOnGroundOrWater"]?.jsonPrimitive?.booleanOrNull
                        ?: return null
                )
            )
        }
        else -> null
    }
}