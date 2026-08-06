package dev.nyon.bbm.movement

import net.minecraft.world.entity.vehicle.boat.AbstractBoat.Status

/** Mirrors the vertical portion of vanilla's AbstractBoat.floatBoat update. */
object BoatVerticalPhysics {
    fun updatedVelocity(
        status: Status,
        verticalVelocity: Double,
        gravity: Double,
        waterLevel: Double,
        boatY: Double,
        boatHeight: Double
    ): Double {
        var acceleration = -gravity
        val buoyancy = when (status) {
            Status.IN_WATER -> if (boatHeight > 0.0) (waterLevel - boatY) / boatHeight else 0.0
            Status.UNDER_FLOWING_WATER -> {
                acceleration = -FLOWING_WATER_ACCELERATION
                0.0
            }
            Status.UNDER_WATER -> UNDERWATER_BUOYANCY
            else -> 0.0
        }

        val acceleratedVelocity = verticalVelocity + acceleration
        return if (buoyancy > 0.0) {
            (acceleratedVelocity + buoyancy * (JumpReachability.DEFAULT_BOAT_GRAVITY / BUOYANCY_DIVISOR)) *
                BUOYANCY_DAMPING
        } else acceleratedVelocity
    }

    /** Predicts the ascent while the boat rises through water and then continues through air. */
    fun maximumRise(
        status: Status,
        verticalVelocity: Double,
        gravity: Double,
        waterSurfaceY: Double,
        boatY: Double,
        boatHeight: Double
    ): Double {
        if (verticalVelocity <= 0.0 || !verticalVelocity.isFinite()) return 0.0
        if (gravity <= 0.0 || !gravity.isFinite()) return Double.POSITIVE_INFINITY

        var predictedStatus = status
        var predictedVelocity = verticalVelocity
        var predictedY = boatY
        var rise = 0.0
        repeat(MAX_ASCENT_TICKS) {
            predictedVelocity = updatedVelocity(
                predictedStatus,
                predictedVelocity,
                gravity,
                waterSurfaceY,
                predictedY,
                boatHeight
            )
            if (!predictedVelocity.isFinite()) return Double.POSITIVE_INFINITY
            if (predictedVelocity <= 0.0) return rise

            predictedY += predictedVelocity
            rise += predictedVelocity
            predictedStatus = statusAfterMovement(status, predictedY, boatHeight, waterSurfaceY)
        }

        // A finite positive ascent should terminate quickly; avoid false rejection if modded physics does not.
        return Double.POSITIVE_INFINITY
    }

    private fun statusAfterMovement(
        initialStatus: Status,
        boatY: Double,
        boatHeight: Double,
        waterSurfaceY: Double
    ): Status {
        if (initialStatus != Status.IN_WATER &&
            initialStatus != Status.UNDER_WATER &&
            initialStatus != Status.UNDER_FLOWING_WATER
        ) return Status.IN_AIR
        if (boatY >= waterSurfaceY) return Status.IN_AIR
        if (boatY + boatHeight + SUBMERGED_EPSILON >= waterSurfaceY) return Status.IN_WATER
        return if (initialStatus == Status.UNDER_FLOWING_WATER) {
            Status.UNDER_FLOWING_WATER
        } else {
            Status.UNDER_WATER
        }
    }

    private const val FLOWING_WATER_ACCELERATION = 7.0e-4
    private const val UNDERWATER_BUOYANCY = 0.01
    private const val BUOYANCY_DIVISOR = 0.65
    private const val BUOYANCY_DAMPING = 0.75
    private const val SUBMERGED_EPSILON = 0.001
    private const val MAX_ASCENT_TICKS = 10_000
}
