package dev.nyon.bbm.paper.movement

import java.util.UUID

data class PaperHorizontalMotion(
    val x: Double,
    val z: Double
) {
    val lengthSquared: Double
        get() = x * x + z * z

    fun preferStronger(other: PaperHorizontalMotion): PaperHorizontalMotion {
        if (other.lengthSquared <= lengthSquared) return this
        if (lengthSquared <= DIRECTION_EPSILON || x * other.x + z * other.z > 0.0) return other
        return this
    }

    private companion object {
        const val DIRECTION_EPSILON = 1.0e-12
    }
}

/**
 * Retains the movement from the tick before a collision. Paper fires its vehicle movement event
 * after Minecraft has removed the blocked velocity component, so the event alone cannot otherwise
 * reproduce the horizontal velocity used by the mod-side boost.
 */
class PaperHorizontalMotionTracker {
    private val movements = mutableMapOf<UUID, TimedMotion>()

    @Synchronized
    fun record(id: UUID, tick: Long, movement: PaperHorizontalMotion): PaperHorizontalMotion {
        val previous = movements.put(id, TimedMotion(tick, movement))
        if (previous == null || tick - previous.tick !in 0L..1L) return movement
        return movement.preferStronger(previous.movement)
    }

    @Synchronized
    fun remove(id: UUID) {
        movements.remove(id)
    }

    @Synchronized
    fun clear() {
        movements.clear()
    }

    private data class TimedMotion(val tick: Long, val movement: PaperHorizontalMotion)
}
