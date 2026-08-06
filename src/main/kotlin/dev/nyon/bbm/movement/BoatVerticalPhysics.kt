package dev.nyon.bbm.movement

import net.minecraft.world.entity.vehicle.boat.AbstractBoat.Status

/** Mirrors the vertical portion of vanilla's current-tick AbstractBoat.floatBoat update. */
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

    private const val FLOWING_WATER_ACCELERATION = 7.0e-4
    private const val UNDERWATER_BUOYANCY = 0.01
    private const val BUOYANCY_DIVISOR = 0.65
    private const val BUOYANCY_DAMPING = 0.75
}
