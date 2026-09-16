package dev.nyon.bbm.paper.movement

import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min

data class PaperAxisAlignedBox(
    val minX: Double,
    val minY: Double,
    val minZ: Double,
    val maxX: Double,
    val maxY: Double,
    val maxZ: Double
) {
    fun intersects(other: PaperAxisAlignedBox): Boolean =
        maxX > other.minX && minX < other.maxX &&
            maxY > other.minY && minY < other.maxY &&
            maxZ > other.minZ && minZ < other.maxZ

    fun overlapsHorizontally(other: PaperAxisAlignedBox): Boolean =
        maxX > other.minX && minX < other.maxX &&
            maxZ > other.minZ && minZ < other.maxZ
}

data class PaperForwardSweep(
    val origin: PaperAxisAlignedBox,
    val deltaX: Double,
    val deltaZ: Double
) {
    val distance: Double = hypot(deltaX, deltaZ)
    val broadphase = PaperAxisAlignedBox(
        min(origin.minX, origin.minX + deltaX),
        origin.minY,
        min(origin.minZ, origin.minZ + deltaZ),
        max(origin.maxX, origin.maxX + deltaX),
        origin.maxY,
        max(origin.maxZ, origin.maxZ + deltaZ)
    )

    fun hitDistance(candidate: PaperAxisAlignedBox): Double? {
        if (distance == 0.0) return null
        if (origin.maxY <= candidate.minY || origin.minY >= candidate.maxY) return null
        if (origin.intersects(candidate)) return null

        val xTimes = axisTimes(origin.minX, origin.maxX, candidate.minX, candidate.maxX, deltaX)
            ?: return null
        val zTimes = axisTimes(origin.minZ, origin.maxZ, candidate.minZ, candidate.maxZ, deltaZ)
            ?: return null
        val rawEntry = max(xTimes.first, zTimes.first)
        if (rawEntry < 0.0) return null
        val entry = max(0.0, rawEntry)
        val exit = min(1.0, min(xTimes.second, zTimes.second))
        return if (entry <= exit) entry * distance else null
    }

    companion object {
        private const val DIRECTION_EPSILON = 1.0e-7

        fun create(
            origin: PaperAxisAlignedBox,
            motionX: Double,
            motionZ: Double,
            facingX: Double,
            facingZ: Double,
            extraRange: Double
        ): PaperForwardSweep {
            val motionLength = hypot(motionX, motionZ)
            val facingLength = hypot(facingX, facingZ)
            val (directionX, directionZ) = when {
                motionLength > DIRECTION_EPSILON -> motionX / motionLength to motionZ / motionLength
                facingLength > DIRECTION_EPSILON -> facingX / facingLength to facingZ / facingLength
                else -> 0.0 to 0.0
            }
            return PaperForwardSweep(
                origin,
                motionX + directionX * extraRange,
                motionZ + directionZ * extraRange
            )
        }

        private fun axisTimes(
            originMin: Double,
            originMax: Double,
            candidateMin: Double,
            candidateMax: Double,
            delta: Double
        ): Pair<Double, Double>? {
            if (delta == 0.0) {
                return if (originMax > candidateMin && originMin < candidateMax) {
                    Double.NEGATIVE_INFINITY to Double.POSITIVE_INFINITY
                } else null
            }
            val first = (candidateMin - originMax) / delta
            val second = (candidateMax - originMin) / delta
            return min(first, second) to max(first, second)
        }
    }
}
