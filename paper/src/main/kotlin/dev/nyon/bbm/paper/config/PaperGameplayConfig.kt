package dev.nyon.bbm.paper.config

import kotlinx.serialization.Serializable

object PaperGameplayConfigLimits {
    const val DEFAULT_STEP_HEIGHT = 0.35f
    const val MIN_STEP_HEIGHT = 0f
    const val MAX_STEP_HEIGHT = 4f

    const val DEFAULT_PLAYER_EJECT_TICKS = 200f
    const val MIN_PLAYER_EJECT_TICKS = 0f
    const val MAX_PLAYER_EJECT_TICKS = 10_000f

    const val DEFAULT_EXTRA_COLLISION_DETECTION_RANGE = 0.5
    const val MIN_EXTRA_COLLISION_DETECTION_RANGE = 0.0
    const val MAX_EXTRA_COLLISION_DETECTION_RANGE = 16.0

    const val DEFAULT_HEIGHT_TOLERANCE = 0.25
    const val MIN_HEIGHT_TOLERANCE = 0.0
    const val MAX_HEIGHT_TOLERANCE = 4.0

    const val DEFAULT_KEYBIND_JUMP_HEIGHT_MULTIPLIER = 1.2
    const val MIN_KEYBIND_JUMP_HEIGHT_MULTIPLIER = 0.0
    const val MAX_KEYBIND_JUMP_HEIGHT_MULTIPLIER = 8.0
}

/** Matches the names and wire ordinals of Minecraft's AbstractBoat.Status. */
@Serializable
enum class PaperBoatStatus(val wireOrdinal: Int) {
    IN_WATER(0),
    UNDER_WATER(1),
    UNDER_FLOWING_WATER(2),
    ON_LAND(3),
    IN_AIR(4)
}

@Serializable
data class PaperGameplayConfig(
    var stepHeight: Float = PaperGameplayConfigLimits.DEFAULT_STEP_HEIGHT,
    var playerEjectTicks: Float = PaperGameplayConfigLimits.DEFAULT_PLAYER_EJECT_TICKS,
    var boosting: Boosting = Boosting(),
    var keybind: Keybind = Keybind()
) {
    @Serializable
    data class Boosting(
        var boostStates: MutableSet<PaperBoatStatus> = mutableSetOf(
            PaperBoatStatus.ON_LAND,
            PaperBoatStatus.IN_WATER,
            PaperBoatStatus.UNDER_FLOWING_WATER,
            PaperBoatStatus.UNDER_WATER
        ),
        var allowedSupportingBlocks: MutableSet<PaperIdentifier> = mutableSetOf(),
        var allowedCollidingBlocks: MutableSet<PaperIdentifier> = mutableSetOf(),
        var onlyForPlayers: Boolean = true,
        var extraCollisionDetectionRange: Double = PaperGameplayConfigLimits.DEFAULT_EXTRA_COLLISION_DETECTION_RANGE,
        var heightTolerance: Double = PaperGameplayConfigLimits.DEFAULT_HEIGHT_TOLERANCE
    )

    @Serializable
    data class Keybind(
        var allowJumpKeybind: Boolean = false,
        var keybindJumpHeightMultiplier: Double =
            PaperGameplayConfigLimits.DEFAULT_KEYBIND_JUMP_HEIGHT_MULTIPLIER,
        var onlyKeybindJumpOnGroundOrWater: Boolean = true
    )
}

data class PaperGameplayConfigSnapshot(
    val stepHeight: Float,
    val playerEjectTicks: Float,
    val boostStates: Set<PaperBoatStatus>,
    val allowedSupportingBlocks: Set<PaperIdentifier>,
    val allowedCollidingBlocks: Set<PaperIdentifier>,
    val onlyForPlayers: Boolean,
    val extraCollisionDetectionRange: Double,
    val heightTolerance: Double,
    val allowJumpKeybind: Boolean,
    val keybindJumpHeightMultiplier: Double,
    val onlyKeybindJumpOnGroundOrWater: Boolean
)

fun PaperGameplayConfig.toSnapshot() = PaperGameplayConfigSnapshot(
    stepHeight = stepHeight.validated(
        PaperGameplayConfigLimits.DEFAULT_STEP_HEIGHT,
        PaperGameplayConfigLimits.MIN_STEP_HEIGHT,
        PaperGameplayConfigLimits.MAX_STEP_HEIGHT
    ),
    playerEjectTicks = playerEjectTicks.validated(
        PaperGameplayConfigLimits.DEFAULT_PLAYER_EJECT_TICKS,
        PaperGameplayConfigLimits.MIN_PLAYER_EJECT_TICKS,
        PaperGameplayConfigLimits.MAX_PLAYER_EJECT_TICKS
    ),
    boostStates = boosting.boostStates.toSet(),
    allowedSupportingBlocks = boosting.allowedSupportingBlocks.toSet(),
    allowedCollidingBlocks = boosting.allowedCollidingBlocks.toSet(),
    onlyForPlayers = boosting.onlyForPlayers,
    extraCollisionDetectionRange = boosting.extraCollisionDetectionRange.validated(
        PaperGameplayConfigLimits.DEFAULT_EXTRA_COLLISION_DETECTION_RANGE,
        PaperGameplayConfigLimits.MIN_EXTRA_COLLISION_DETECTION_RANGE,
        PaperGameplayConfigLimits.MAX_EXTRA_COLLISION_DETECTION_RANGE
    ),
    heightTolerance = boosting.heightTolerance.validated(
        PaperGameplayConfigLimits.DEFAULT_HEIGHT_TOLERANCE,
        PaperGameplayConfigLimits.MIN_HEIGHT_TOLERANCE,
        PaperGameplayConfigLimits.MAX_HEIGHT_TOLERANCE
    ),
    allowJumpKeybind = keybind.allowJumpKeybind,
    keybindJumpHeightMultiplier = keybind.keybindJumpHeightMultiplier.validated(
        PaperGameplayConfigLimits.DEFAULT_KEYBIND_JUMP_HEIGHT_MULTIPLIER,
        PaperGameplayConfigLimits.MIN_KEYBIND_JUMP_HEIGHT_MULTIPLIER,
        PaperGameplayConfigLimits.MAX_KEYBIND_JUMP_HEIGHT_MULTIPLIER
    ),
    onlyKeybindJumpOnGroundOrWater = keybind.onlyKeybindJumpOnGroundOrWater
)

private fun Float.validated(default: Float, minimum: Float, maximum: Float): Float =
    if (isFinite()) coerceIn(minimum, maximum) else default

private fun Double.validated(default: Double, minimum: Double, maximum: Double): Double =
    if (isFinite()) coerceIn(minimum, maximum) else default
