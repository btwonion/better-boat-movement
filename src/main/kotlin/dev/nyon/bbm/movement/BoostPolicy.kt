package dev.nyon.bbm.movement

enum class BoostTrigger {
    AUTOMATIC,
    MANUAL
}

data class BoatContext(
    val trigger: BoostTrigger,
    val triggerPresent: Boolean,
    val stateEnabled: Boolean,
    val hasPlayerPassenger: Boolean,
    val playerRequired: Boolean,
    val supportingBlockAllowed: Boolean,
    val collidingBlockAllowed: Boolean,
    val obstacleReachable: Boolean = true,
    val groundedOrInWater: Boolean,
    val manualGroundOrWaterRequired: Boolean,
    val boostHeight: Double
)

enum class RejectionReason {
    NO_TRIGGER,
    STATE_DISABLED,
    PLAYER_REQUIRED,
    SUPPORTING_BLOCK_DISALLOWED,
    COLLIDING_BLOCK_DISALLOWED,
    OBSTACLE_TOO_HIGH,
    MANUAL_JUMP_DISABLED_IN_AIR
}

data class BoostDecision(
    val boostHeight: Double?,
    val rejectionReason: RejectionReason? = null
) {
    val shouldBoost: Boolean get() = boostHeight != null

    companion object {
        fun boost(height: Double) = BoostDecision(height)
        fun reject(reason: RejectionReason) = BoostDecision(null, reason)
    }
}

object BoostPolicy {
    fun decide(context: BoatContext): BoostDecision {
        if (!context.triggerPresent) return BoostDecision.reject(RejectionReason.NO_TRIGGER)
        if (context.trigger == BoostTrigger.MANUAL) {
            if (context.manualGroundOrWaterRequired && !context.groundedOrInWater) {
                return BoostDecision.reject(RejectionReason.MANUAL_JUMP_DISABLED_IN_AIR)
            }
            return BoostDecision.boost(context.boostHeight)
        }

        if (!context.stateEnabled) return BoostDecision.reject(RejectionReason.STATE_DISABLED)
        if (context.playerRequired && !context.hasPlayerPassenger) {
            return BoostDecision.reject(RejectionReason.PLAYER_REQUIRED)
        }
        if (!context.supportingBlockAllowed) {
            return BoostDecision.reject(RejectionReason.SUPPORTING_BLOCK_DISALLOWED)
        }
        if (!context.collidingBlockAllowed) {
            return BoostDecision.reject(RejectionReason.COLLIDING_BLOCK_DISALLOWED)
        }
        if (!context.obstacleReachable) {
            return BoostDecision.reject(RejectionReason.OBSTACLE_TOO_HIGH)
        }
        return BoostDecision.boost(context.boostHeight)
    }
}
