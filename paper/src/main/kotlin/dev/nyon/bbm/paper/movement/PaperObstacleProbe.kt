package dev.nyon.bbm.paper.movement

import dev.nyon.bbm.paper.config.PaperGameplayConfigSnapshot
import org.bukkit.block.Block
import org.bukkit.entity.Boat
import kotlin.math.floor

data class PaperObstacleHit(
    val block: Block,
    val distance: Double,
    val topY: Double
)

object PaperObstacleProbe {
    fun findAhead(boat: Boat, config: PaperGameplayConfigSnapshot): PaperObstacleHit? {
        val velocity = boat.velocity
        val facing = boat.location.direction
        val probeRange = if (config.extraCollisionDetectionRange == 0.0) {
            CONTACT_PROBE_RANGE
        } else config.extraCollisionDetectionRange
        val sweep = PaperForwardSweep.create(
            boat.boundingBox.toCoreBox(),
            velocity.x,
            velocity.z,
            facing.x,
            facing.z,
            probeRange
        )
        if (sweep.distance == 0.0) return null

        val broadphase = sweep.broadphase
        var nearest: PaperObstacleHit? = null
        for (x in floor(broadphase.minX).toInt()..floor(broadphase.maxX).toInt()) {
            for (y in floor(broadphase.minY).toInt()..floor(broadphase.maxY).toInt()) {
                for (z in floor(broadphase.minZ).toInt()..floor(broadphase.maxZ).toInt()) {
                    val block = boat.world.getBlockAt(x, y, z)
                    if (block.type.isAir) continue
                    for (box in block.collisionShape.boundingBoxes) {
                        val candidate = box.toCoreBox(x.toDouble(), y.toDouble(), z.toDouble())
                        val distance = sweep.hitDistance(candidate) ?: continue
                        val topY = findObstacleTop(block, candidate)
                        if (nearest == null || distance < nearest.distance ||
                            distance == nearest.distance && topY > nearest.topY
                        ) {
                            nearest = PaperObstacleHit(block, distance, topY)
                        }
                    }
                }
            }
        }
        return nearest
    }

    private fun findObstacleTop(block: Block, contact: PaperAxisAlignedBox): Double {
        var top = contact.maxY
        var y = block.y
        while (true) {
            val current = block.world.getBlockAt(block.x, y, block.z)
            var extendedTop = top
            var changed: Boolean
            do {
                changed = false
                for (box in current.collisionShape.boundingBoxes.map {
                    it.toCoreBox(current.x.toDouble(), current.y.toDouble(), current.z.toDouble())
                }) {
                    if (box.overlapsHorizontally(contact) &&
                        box.minY <= extendedTop + COLLISION_EPSILON &&
                        box.maxY > extendedTop + COLLISION_EPSILON
                    ) {
                        extendedTop = box.maxY
                        changed = true
                    }
                }
            } while (changed)
            if (extendedTop > top + COLLISION_EPSILON) top = extendedTop

            y++
            val next = block.world.getBlockAt(block.x, y, block.z)
            val continues = next.collisionShape.boundingBoxes.any { rawBox ->
                val box = rawBox.toCoreBox(next.x.toDouble(), next.y.toDouble(), next.z.toDouble())
                box.overlapsHorizontally(contact) &&
                    box.minY <= top + COLLISION_EPSILON &&
                    box.maxY > top + COLLISION_EPSILON
            }
            if (!continues) break
        }
        return top
    }

    private const val COLLISION_EPSILON = 1.0e-7
    internal const val CONTACT_PROBE_RANGE = 0.001
}

internal fun org.bukkit.util.BoundingBox.toCoreBox(
    offsetX: Double = 0.0,
    offsetY: Double = 0.0,
    offsetZ: Double = 0.0
) = PaperAxisAlignedBox(
    minX + offsetX,
    minY + offsetY,
    minZ + offsetZ,
    maxX + offsetX,
    maxY + offsetY,
    maxZ + offsetZ
)
