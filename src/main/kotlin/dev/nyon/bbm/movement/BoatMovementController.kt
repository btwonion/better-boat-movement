package dev.nyon.bbm.movement

import dev.nyon.bbm.config.ConfigRepository
import dev.nyon.bbm.config.filtersFor
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.vehicle.boat.AbstractBoat
import net.minecraft.world.phys.Vec3

object BoatMovementController {
    fun automaticVelocity(boat: AbstractBoat, status: AbstractBoat.Status, original: Vec3): Vec3 {
        val config = ConfigRepository.snapshotFor(boat.level().isClientSide) ?: return original
        val filters = filtersFor(config)
        val obstacle = ObstacleProbe.findAhead(boat, config, filters.colliding)
        val triggerPresent = boat.horizontalCollision ||
            config.extraCollisionDetectionRange > 0.0 && obstacle != null
        val collidingAllowed = filters.colliding.isEmpty() || obstacle != null
        val supportAllowed = status != AbstractBoat.Status.ON_LAND ||
            SupportingBlockProbe.hasAllowedSupport(boat, filters.supporting)
        val decision = BoostPolicy.decide(
            BoatContext(
                trigger = BoostTrigger.AUTOMATIC,
                triggerPresent = triggerPresent,
                stateEnabled = status in config.boostStates,
                hasPlayerPassenger = boat.passengers.any { it is Player },
                playerRequired = config.onlyForPlayers,
                supportingBlockAllowed = supportAllowed,
                collidingBlockAllowed = collidingAllowed,
                obstacleReachable = obstacle == null || JumpReachability.canReach(
                    boat.boundingBox.minY,
                    obstacle.topY,
                    config.stepHeight.toDouble(),
                    config.heightTolerance
                ),
                groundedOrInWater = false,
                manualGroundOrWaterRequired = false,
                boostHeight = config.stepHeight.toDouble()
            )
        )
        return if (decision.shouldBoost) Vec3(original.x, decision.boostHeight!!, original.z) else original
    }

    fun tryManualJump(boat: AbstractBoat): Boolean {
        val config = ConfigRepository.snapshotFor(boat.level().isClientSide) ?: return false
        if (!config.allowJumpKeybind) return false
        val decision = BoostPolicy.decide(
            BoatContext(
                trigger = BoostTrigger.MANUAL,
                triggerPresent = true,
                stateEnabled = true,
                hasPlayerPassenger = true,
                playerRequired = false,
                supportingBlockAllowed = true,
                collidingBlockAllowed = true,
                groundedOrInWater = boat.onGround() || boat.isInWater || boat.isUnderWater,
                manualGroundOrWaterRequired = config.onlyKeybindJumpOnGroundOrWater,
                boostHeight = config.stepHeight * config.keybindJumpHeightMultiplier
            )
        )
        if (!decision.shouldBoost) return false
        boat.addDeltaMovement(Vec3(0.0, decision.boostHeight!!, 0.0))
        return true
    }
}
