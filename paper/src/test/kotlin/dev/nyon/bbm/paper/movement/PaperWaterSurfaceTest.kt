package dev.nyon.bbm.paper.movement

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PaperWaterSurfaceTest {
    @Test
    fun `uses the fractional height of uncovered source water`() {
        assertEquals((8.0f / 9.0f).toDouble(), fluidSurfaceHeight(level = 0, coveredByWater = false))
    }

    @Test
    fun `uses the fractional height of uncovered flowing water`() {
        assertEquals((7.0f / 9.0f).toDouble(), fluidSurfaceHeight(level = 1, coveredByWater = false))
        assertEquals((1.0f / 9.0f).toDouble(), fluidSurfaceHeight(level = 7, coveredByWater = false))
    }

    @Test
    fun `treats falling water as a full fluid amount`() {
        assertEquals((8.0f / 9.0f).toDouble(), fluidSurfaceHeight(level = 12, coveredByWater = false))
    }

    @Test
    fun `fills the block when water continues above it`() {
        assertEquals(1.0, fluidSurfaceHeight(level = 7, coveredByWater = true))
    }
}
