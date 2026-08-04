package dev.nyon.bbm.movement

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ForwardSweepTest {
    private val unitBoat = AxisAlignedBox(0.0, 0.0, 0.0, 1.0, 1.0, 1.0)

    @Test
    fun `cardinal probe reaches configured distance only in front`() {
        val sweep = ForwardSweep.create(unitBoat, 0.1, 0.0, 1.0, 0.0, 0.5)
        assertEquals(0.4, assertNotNull(sweep.hitDistance(box(1.4, 0.0))), 1.0e-9)
        assertNull(sweep.hitDistance(box(-1.0, 0.0)))
        assertNull(sweep.hitDistance(box(0.0, 1.01)))
    }

    @Test
    fun `diagonal sweep excludes empty broadphase corners`() {
        val sweep = ForwardSweep.create(unitBoat, 1.0, 1.0, 1.0, 1.0, 0.5)
        assertNotNull(sweep.hitDistance(box(2.0, 2.0)))
        assertNull(sweep.hitDistance(box(2.0, -0.75, width = 0.5)))
    }

    @Test
    fun `low speed falls back to facing direction`() {
        val sweep = ForwardSweep.create(unitBoat, 0.0, 0.0, 0.0, -1.0, 0.5)
        assertNotNull(sweep.hitDistance(box(0.0, -0.4, width = 0.4)))
        assertNull(sweep.hitDistance(box(0.0, 1.1)))
    }

    @Test
    fun `negative coordinates and partial height boxes are handled geometrically`() {
        val origin = AxisAlignedBox(-2.0, 0.5, -2.0, -1.0, 1.5, -1.0)
        val sweep = ForwardSweep.create(origin, -0.25, 0.0, -1.0, 0.0, 0.5)
        val slab = AxisAlignedBox(-2.6, 0.0, -2.0, -2.0, 0.75, -1.0)
        assertEquals(0.0, assertNotNull(sweep.hitDistance(slab)), 1.0e-9)
        val below = AxisAlignedBox(-2.6, 0.0, -2.0, -2.0, 0.49, -1.0)
        assertNull(sweep.hitDistance(below))
    }

    @Test
    fun `range zero contains movement but adds no inflation`() {
        val sweep = ForwardSweep.create(unitBoat, 0.25, 0.0, 1.0, 0.0, 0.0)
        assertEquals(0.25, sweep.distance, 1.0e-9)
        assertNotNull(sweep.hitDistance(box(1.2, 0.0)))
        assertNull(sweep.hitDistance(box(1.3, 0.0)))
    }

    private fun box(x: Double, z: Double, width: Double = 1.0) =
        AxisAlignedBox(x, 0.0, z, x + width, 1.0, z + width)
}
