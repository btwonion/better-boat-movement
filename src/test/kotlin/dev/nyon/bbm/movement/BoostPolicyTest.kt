package dev.nyon.bbm.movement

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class BoostPolicyTest {
    @Test
    fun `a present trigger from a disallowed nearest collision is rejected`() {
        val decision = BoostPolicy.decide(
            BoatContext(
                trigger = BoostTrigger.AUTOMATIC,
                triggerPresent = true,
                stateEnabled = true,
                hasPlayerPassenger = true,
                playerRequired = true,
                supportingBlockAllowed = true,
                collidingBlockAllowed = false,
                obstacleReachable = true,
                groundedOrInWater = true,
                manualGroundOrWaterRequired = false,
                boostHeight = 0.35
            )
        )

        assertFalse(decision.shouldBoost)
        assertEquals(RejectionReason.COLLIDING_BLOCK_DISALLOWED, decision.rejectionReason)
    }
}
