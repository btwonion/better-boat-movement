package dev.nyon.bbm.paper.movement

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.UUID

class PaperHorizontalMotionTest {
    private val boatId = UUID.randomUUID()

    @Test
    fun `retains previous horizontal motion when a head-on collision stops the boat`() {
        val tracker = PaperHorizontalMotionTracker()
        tracker.record(boatId, 10, PaperHorizontalMotion(0.3, 0.0))

        val collisionMotion = tracker.record(boatId, 11, PaperHorizontalMotion(0.0, 0.0))

        assertEquals(PaperHorizontalMotion(0.3, 0.0), collisionMotion)
    }

    @Test
    fun `retains the blocked component at a corner`() {
        val tracker = PaperHorizontalMotionTracker()
        tracker.record(boatId, 20, PaperHorizontalMotion(0.2, 0.2))

        val collisionMotion = tracker.record(boatId, 21, PaperHorizontalMotion(0.0, 0.2))

        assertEquals(PaperHorizontalMotion(0.2, 0.2), collisionMotion)
    }

    @Test
    fun `does not restore stale movement`() {
        val tracker = PaperHorizontalMotionTracker()
        tracker.record(boatId, 30, PaperHorizontalMotion(0.3, 0.0))

        val movement = tracker.record(boatId, 32, PaperHorizontalMotion(0.0, 0.0))

        assertEquals(PaperHorizontalMotion(0.0, 0.0), movement)
    }

    @Test
    fun `does not restore movement in the opposite direction`() {
        val tracker = PaperHorizontalMotionTracker()
        tracker.record(boatId, 40, PaperHorizontalMotion(0.3, 0.0))

        val movement = tracker.record(boatId, 41, PaperHorizontalMotion(-0.1, 0.0))

        assertEquals(PaperHorizontalMotion(-0.1, 0.0), movement)
    }
}
