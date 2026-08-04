package dev.nyon.bbm.config

import com.mojang.logging.LogUtils

object ConfigValidator {
    private val logger = LogUtils.getLogger()

    fun snapshot(config: GameplayConfig): GameplayConfigSnapshot = GameplayConfigSnapshot(
        stepHeight = finiteFloat("stepHeight", config.stepHeight, 0.35f, 0f, 16f),
        playerEjectTicks = finiteFloat("playerEjectTicks", config.playerEjectTicks, 200f, 0f, 12_000f),
        boostStates = config.boosting.boostStates.toSet(),
        allowedSupportingBlocks = config.boosting.allowedSupportingBlocks.toSet(),
        allowedCollidingBlocks = config.boosting.allowedCollidingBlocks.toSet(),
        onlyForPlayers = config.boosting.onlyForPlayers,
        extraCollisionDetectionRange = finiteDouble(
            "extraCollisionDetectionRange",
            config.boosting.extraCollisionDetectionRange,
            0.5,
            0.0,
            16.0
        ),
        heightTolerance = finiteDouble(
            "heightTolerance",
            config.boosting.heightTolerance,
            0.25,
            0.0,
            16.0
        ),
        allowJumpKeybind = config.keybind.allowJumpKeybind,
        keybindJumpHeightMultiplier = finiteDouble(
            "keybindJumpHeightMultiplier",
            config.keybind.keybindJumpHeightMultiplier,
            1.2,
            0.0,
            16.0
        ),
        onlyKeybindJumpOnGroundOrWater = config.keybind.onlyKeybindJumpOnGroundOrWater
    )

    private fun finiteFloat(name: String, value: Float, fallback: Float, min: Float, max: Float): Float {
        val validated = if (value.isFinite()) value.coerceIn(min, max) else fallback
        if (validated != value) logger.warn("Invalid BBM {} value {}; using {}", name, value, validated)
        return validated
    }

    private fun finiteDouble(name: String, value: Double, fallback: Double, min: Double, max: Double): Double {
        val validated = if (value.isFinite()) value.coerceIn(min, max) else fallback
        if (validated != value) logger.warn("Invalid BBM {} value {}; using {}", name, value, validated)
        return validated
    }
}
