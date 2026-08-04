package dev.nyon.bbm.movement

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BoostPolicyTest {
    private fun automaticContext() = BoatContext(
        trigger = BoostTrigger.AUTOMATIC,
        triggerPresent = true,
        stateEnabled = true,
        hasPlayerPassenger = true,
        playerRequired = true,
        supportingBlockAllowed = true,
        collidingBlockAllowed = true,
        groundedOrInWater = true,
        manualGroundOrWaterRequired = false,
        boostHeight = 0.35
    )

    @Test
    fun `automatic boost preserves configured height`() {
        val decision = BoostPolicy.decide(automaticContext())
        assertTrue(decision.shouldBoost)
        assertEquals(0.35, decision.boostHeight)
    }

    @Test
    fun `a disallowed collision cannot inherit a prior allowed result`() {
        assertTrue(BoostPolicy.decide(automaticContext()).shouldBoost)
        val nextTick = BoostPolicy.decide(automaticContext().copy(collidingBlockAllowed = false))
        assertFalse(nextTick.shouldBoost)
        assertEquals(RejectionReason.COLLIDING_BLOCK_DISALLOWED, nextTick.rejectionReason)
    }

    @Test
    fun `automatic boost rejects an obstacle above its reach`() {
        val decision = BoostPolicy.decide(automaticContext().copy(obstacleReachable = false))
        assertFalse(decision.shouldBoost)
        assertEquals(RejectionReason.OBSTACLE_TOO_HIGH, decision.rejectionReason)
    }

    @Test
    fun `player support state and trigger restrictions are independent`() {
        val cases = listOf(
            automaticContext().copy(triggerPresent = false) to RejectionReason.NO_TRIGGER,
            automaticContext().copy(stateEnabled = false) to RejectionReason.STATE_DISABLED,
            automaticContext().copy(hasPlayerPassenger = false) to RejectionReason.PLAYER_REQUIRED,
            automaticContext().copy(supportingBlockAllowed = false) to RejectionReason.SUPPORTING_BLOCK_DISALLOWED
        )
        cases.forEach { (context, expected) ->
            assertEquals(expected, BoostPolicy.decide(context).rejectionReason)
        }
    }

    @Test
    fun `manual boost rejects midair use only when configured`() {
        val context = automaticContext().copy(
            trigger = BoostTrigger.MANUAL,
            groundedOrInWater = false,
            manualGroundOrWaterRequired = true,
            boostHeight = 0.42
        )
        assertEquals(RejectionReason.MANUAL_JUMP_DISABLED_IN_AIR, BoostPolicy.decide(context).rejectionReason)
        assertTrue(BoostPolicy.decide(context.copy(manualGroundOrWaterRequired = false)).shouldBoost)
    }
}
