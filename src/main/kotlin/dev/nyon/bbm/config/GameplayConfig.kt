package dev.nyon.bbm.config

import kotlinx.serialization.Serializable
import net.minecraft.world.entity.vehicle.boat.AbstractBoat.Status

/** Mutable on-disk representation. Runtime code consumes [GameplayConfigSnapshot] instead. */
@Serializable
data class GameplayConfig(
    var stepHeight: Float = 0.35f,
    var playerEjectTicks: Float = 20f * 10f,
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
        var extraCollisionDetectionRange: Double = 0.5,
        var heightTolerance: Double = 0.25
    )

    @Serializable
    data class Keybind(
        var allowJumpKeybind: Boolean = false,
        var keybindJumpHeightMultiplier: Double = 1.2,
        var onlyKeybindJumpOnGroundOrWater: Boolean = true
    )
}

/** Immutable, validated gameplay state shared by movement and networking. */
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
