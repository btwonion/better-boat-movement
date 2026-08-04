package dev.nyon.bbm.movement

object JumpReachability {
    fun canReach(
        boatBottomY: Double,
        obstacleTopY: Double,
        jumpHeight: Double,
        heightTolerance: Double
    ): Boolean = obstacleTopY - boatBottomY <= jumpHeight + heightTolerance
}
