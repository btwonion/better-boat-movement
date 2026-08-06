package dev.nyon.bbm.movement

object JumpReachability {
    fun canReach(
        boatBottomY: Double,
        obstacleTopY: Double,
        jumpVelocity: Double,
        heightTolerance: Double,
        gravity: Double = DEFAULT_BOAT_GRAVITY
    ): Boolean = obstacleTopY - boatBottomY <= maximumRise(jumpVelocity, gravity) + heightTolerance

    /**
     * Predicts the upward distance covered by Minecraft's discrete boat motion. The automatic
     * boost is a velocity, and gravity is applied before each movement tick.
     */
    internal fun maximumRise(jumpVelocity: Double, gravity: Double): Double {
        if (jumpVelocity <= 0.0 || !jumpVelocity.isFinite()) return 0.0
        if (gravity <= 0.0 || !gravity.isFinite()) return Double.POSITIVE_INFINITY

        val upwardTicks = kotlin.math.ceil(jumpVelocity / gravity).toLong() - 1L
        if (upwardTicks <= 0L) return 0.0
        return upwardTicks * jumpVelocity - gravity * upwardTicks * (upwardTicks + 1L) / 2.0
    }

    private const val DEFAULT_BOAT_GRAVITY = 0.04
}
