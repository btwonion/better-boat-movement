package dev.nyon.bbm.movement

object JumpReachability {
    fun canReachAfterUpdate(
        boatBottomY: Double,
        obstacleTopY: Double,
        updatedVerticalVelocity: Double,
        heightTolerance: Double,
        gravity: Double = DEFAULT_BOAT_GRAVITY
    ): Boolean = obstacleTopY - boatBottomY <=
        maximumRiseAfterUpdate(updatedVerticalVelocity, gravity) + heightTolerance

    /**
     * Predicts the upward distance starting with the velocity that vanilla has already updated
     * for the current tick. Later ticks use the boat's normal gravity-only airborne motion.
     */
    internal fun maximumRiseAfterUpdate(updatedVerticalVelocity: Double, gravity: Double): Double {
        if (updatedVerticalVelocity <= 0.0 || !updatedVerticalVelocity.isFinite()) return 0.0
        if (gravity <= 0.0 || !gravity.isFinite()) return Double.POSITIVE_INFINITY

        val upwardTicks = kotlin.math.ceil(updatedVerticalVelocity / gravity).toLong()
        return upwardTicks * updatedVerticalVelocity -
            gravity * upwardTicks * (upwardTicks - 1L) / 2.0
    }

    internal const val DEFAULT_BOAT_GRAVITY = 0.04
}
