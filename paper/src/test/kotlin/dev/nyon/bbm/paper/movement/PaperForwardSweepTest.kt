package dev.nyon.bbm.paper.movement

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.bukkit.util.BoundingBox

class PaperForwardSweepTest {
    private val boat = PaperAxisAlignedBox(0.0, 0.0, 0.0, 1.0, 0.6, 1.0)

    @Test
    fun `reports distance to the first forward collision`() {
        val sweep = PaperForwardSweep.create(boat, 0.2, 0.0, 1.0, 0.0, 0.5)
        val block = PaperAxisAlignedBox(1.4, 0.0, 0.0, 2.4, 1.0, 1.0)

        assertEquals(0.4, sweep.hitDistance(block)!!, 1.0e-9)
    }

    @Test
    fun `ignores blocks outside the vertical hull span`() {
        val sweep = PaperForwardSweep.create(boat, 0.2, 0.0, 1.0, 0.0, 0.5)
        val overhead = PaperAxisAlignedBox(1.4, 0.6, 0.0, 2.4, 1.6, 1.0)

        assertNull(sweep.hitDistance(overhead))
    }

    @Test
    fun `translates block-local collision shapes into world coordinates`() {
        val localShape = BoundingBox(0.125, 0.0, 0.25, 0.875, 0.5, 0.75)

        assertEquals(
            PaperAxisAlignedBox(10.125, 64.0, -3.75, 10.875, 64.5, -3.25),
            localShape.toCoreBox(10.0, 64.0, -4.0)
        )
    }
}
