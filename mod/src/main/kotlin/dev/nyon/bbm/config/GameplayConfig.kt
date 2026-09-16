package dev.nyon.bbm.config

import kotlinx.serialization.Serializable
import net.minecraft.world.entity.vehicle.boat.AbstractBoat.Status

object GameplayConfigLimits {
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

/** Mutable on-disk representation. Runtime code consumes an immutable [GameplayConfigSnapshot]. */
@Serializable
data class GameplayConfig(
    var stepHeight: Float = GameplayConfigLimits.DEFAULT_STEP_HEIGHT,
    var playerEjectTicks: Float = GameplayConfigLimits.DEFAULT_PLAYER_EJECT_TICKS,
    var boosting: Boosting = Boosting(),
    var keybind: Keybind = Keybind()
) {
    @Serializable
    data class Boosting(
        var boostStates: MutableSet<Status> = mutableSetOf(
            Status.ON_LAND,
            Status.IN_WATER,
            Status.UNDER_FLOWING_WATER,
            Status.UNDER_WATER
        ),
        var allowedSupportingBlocks: MutableSet<Identifier> = mutableSetOf(),
        var allowedCollidingBlocks: MutableSet<Identifier> = mutableSetOf(),
        var onlyForPlayers: Boolean = true,
        var extraCollisionDetectionRange: Double = GameplayConfigLimits.DEFAULT_EXTRA_COLLISION_DETECTION_RANGE,
        var heightTolerance: Double = GameplayConfigLimits.DEFAULT_HEIGHT_TOLERANCE
    )

    @Serializable
    data class Keybind(
        var allowJumpKeybind: Boolean = false,
        var keybindJumpHeightMultiplier: Double = GameplayConfigLimits.DEFAULT_KEYBIND_JUMP_HEIGHT_MULTIPLIER,
        var onlyKeybindJumpOnGroundOrWater: Boolean = true
    )
}

/** Immutable gameplay state shared by movement and networking. */
data class GameplayConfigSnapshot(
    val stepHeight: Float,
    val playerEjectTicks: Float,
    val boostStates: Set<Status>,
    val allowedSupportingBlocks: Set<Identifier>,
    val allowedCollidingBlocks: Set<Identifier>,
    val onlyForPlayers: Boolean,
    val extraCollisionDetectionRange: Double,
    val heightTolerance: Double,
    val allowJumpKeybind: Boolean,
    val keybindJumpHeightMultiplier: Double,
    val onlyKeybindJumpOnGroundOrWater: Boolean
) {
    fun toMutableConfig() = GameplayConfig(
        stepHeight = stepHeight,
        playerEjectTicks = playerEjectTicks,
        boosting = GameplayConfig.Boosting(
            boostStates.toMutableSet(),
            allowedSupportingBlocks.toMutableSet(),
            allowedCollidingBlocks.toMutableSet(),
            onlyForPlayers,
            extraCollisionDetectionRange,
            heightTolerance
        ),
        keybind = GameplayConfig.Keybind(
            allowJumpKeybind,
            keybindJumpHeightMultiplier,
            onlyKeybindJumpOnGroundOrWater
        )
    )
}

fun GameplayConfig.toSnapshot() = GameplayConfigSnapshot(
    stepHeight = stepHeight.validated(
        GameplayConfigLimits.DEFAULT_STEP_HEIGHT,
        GameplayConfigLimits.MIN_STEP_HEIGHT,
        GameplayConfigLimits.MAX_STEP_HEIGHT
    ),
    playerEjectTicks = playerEjectTicks.validated(
        GameplayConfigLimits.DEFAULT_PLAYER_EJECT_TICKS,
        GameplayConfigLimits.MIN_PLAYER_EJECT_TICKS,
        GameplayConfigLimits.MAX_PLAYER_EJECT_TICKS
    ),
    boostStates = boosting.boostStates.toSet(),
    allowedSupportingBlocks = boosting.allowedSupportingBlocks.toSet(),
    allowedCollidingBlocks = boosting.allowedCollidingBlocks.toSet(),
    onlyForPlayers = boosting.onlyForPlayers,
    extraCollisionDetectionRange = boosting.extraCollisionDetectionRange.validated(
        GameplayConfigLimits.DEFAULT_EXTRA_COLLISION_DETECTION_RANGE,
        GameplayConfigLimits.MIN_EXTRA_COLLISION_DETECTION_RANGE,
        GameplayConfigLimits.MAX_EXTRA_COLLISION_DETECTION_RANGE
    ),
    heightTolerance = boosting.heightTolerance.validated(
        GameplayConfigLimits.DEFAULT_HEIGHT_TOLERANCE,
        GameplayConfigLimits.MIN_HEIGHT_TOLERANCE,
        GameplayConfigLimits.MAX_HEIGHT_TOLERANCE
    ),
    allowJumpKeybind = keybind.allowJumpKeybind,
    keybindJumpHeightMultiplier = keybind.keybindJumpHeightMultiplier.validated(
        GameplayConfigLimits.DEFAULT_KEYBIND_JUMP_HEIGHT_MULTIPLIER,
        GameplayConfigLimits.MIN_KEYBIND_JUMP_HEIGHT_MULTIPLIER,
        GameplayConfigLimits.MAX_KEYBIND_JUMP_HEIGHT_MULTIPLIER
    ),
    onlyKeybindJumpOnGroundOrWater = keybind.onlyKeybindJumpOnGroundOrWater
)

private fun Float.validated(default: Float, minimum: Float, maximum: Float): Float =
    if (isFinite()) coerceIn(minimum, maximum) else default

private fun Double.validated(default: Double, minimum: Double, maximum: Double): Double =
    if (isFinite()) coerceIn(minimum, maximum) else default
