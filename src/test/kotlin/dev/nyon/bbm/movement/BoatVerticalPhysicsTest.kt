package dev.nyon.bbm.movement

import net.minecraft.world.entity.vehicle.boat.AbstractBoat.Status
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BoatVerticalPhysicsTest {
    @Test
    fun `land applies gravity before the current movement`() {
        val updated = updatedVelocity(Status.ON_LAND)

        assertEquals(0.31, updated, 1.0e-12)
        assertEquals(1.36, JumpReachability.maximumRiseAfterUpdate(updated, GRAVITY), 1.0e-12)
    }

    @Test
    fun `flowing water uses its status acceleration`() {
        assertEquals(0.3493, updatedVelocity(Status.UNDER_FLOWING_WATER), 1.0e-12)
    }

    @Test
    fun `underwater buoyancy and damping reduce the initial boost`() {
        val updated = updatedVelocity(Status.UNDER_WATER)
        val expected = (JUMP_VELOCITY - GRAVITY + 0.01 * (GRAVITY / 0.65)) * 0.75

        assertEquals(expected, updated, 1.0e-12)
        assertTrue(updated < updatedVelocity(Status.ON_LAND))
    }

    @Test
    fun `surface buoyancy uses the current waterline`() {
        val updated = BoatVerticalPhysics.updatedVelocity(
            status = Status.IN_WATER,
            verticalVelocity = JUMP_VELOCITY,
            gravity = GRAVITY,
            waterLevel = 0.5,
            boatY = 0.0,
            boatHeight = 1.0
        )
        val expected = (JUMP_VELOCITY - GRAVITY + 0.5 * (GRAVITY / 0.65)) * 0.75

        assertEquals(expected, updated, 1.0e-12)
    }

    @Test
    fun `reachability uses the status-adjusted velocity`() {
        val landRise = JumpReachability.maximumRiseAfterUpdate(updatedVelocity(Status.ON_LAND), GRAVITY)
        val underwaterRise = JumpReachability.maximumRiseAfterUpdate(updatedVelocity(Status.UNDER_WATER), GRAVITY)
        val obstacleTop = (landRise + underwaterRise) / 2.0

        assertTrue(
            JumpReachability.canReachAfterUpdate(0.0, obstacleTop, updatedVelocity(Status.ON_LAND), 0.0, GRAVITY)
        )
        assertFalse(
            JumpReachability.canReachAfterUpdate(
                0.0,
                obstacleTop,
                updatedVelocity(Status.UNDER_WATER),
                0.0,
                GRAVITY
            )
        )
    }

    private fun updatedVelocity(status: Status): Double = BoatVerticalPhysics.updatedVelocity(
        status = status,
        verticalVelocity = JUMP_VELOCITY,
        gravity = GRAVITY,
        waterLevel = 0.0,
        boatY = 0.0,
        boatHeight = 1.0
    )

    private companion object {
        const val JUMP_VELOCITY = 0.35
        const val GRAVITY = 0.04
    }
}
