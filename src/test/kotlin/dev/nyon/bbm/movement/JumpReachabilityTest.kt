package dev.nyon.bbm.movement

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class JumpReachabilityTest {
    @Test
    fun `one block jump does not attempt four block obstacle`() {
        assertFalse(JumpReachability.canReach(64.0, 68.0, 1.0, 0.25))
    }

    @Test
    fun `jump can reach an obstacle within the clearance tolerance`() {
        assertTrue(JumpReachability.canReach(64.0, 65.0, 0.75, 0.25))
    }

    @Test
    fun `lower configured tolerance rejects the same obstacle`() {
        assertFalse(JumpReachability.canReach(64.0, 65.0, 0.75, 0.24))
    }
}
