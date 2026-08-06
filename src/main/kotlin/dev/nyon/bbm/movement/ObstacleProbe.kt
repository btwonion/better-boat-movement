package dev.nyon.bbm.movement

import dev.nyon.bbm.config.GameplayConfigSnapshot
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.vehicle.boat.AbstractBoat
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.shapes.CollisionContext

data class ObstacleHit(
    val position: BlockPos,
    val state: BlockState,
    val distance: Double,
    val topY: Double
)

object ObstacleProbe {
    fun findAhead(
        boat: AbstractBoat,
        config: GameplayConfigSnapshot
    ): ObstacleHit? {
        val box = boat.boundingBox.toCoreBox()
        val movement = boat.deltaMovement
        val facing = boat.lookAngle
        val sweep = ForwardSweep.create(
            box,
            movement.x,
            movement.z,
            facing.x,
            facing.z,
            config.extraCollisionDetectionRange
        )
        if (sweep.distance == 0.0) return null

        val broadphase = sweep.broadphase
        val minX = kotlin.math.floor(broadphase.minX).toInt()
        val minY = kotlin.math.floor(broadphase.minY).toInt()
        val minZ = kotlin.math.floor(broadphase.minZ).toInt()
        val maxX = kotlin.math.floor(broadphase.maxX).toInt()
        val maxY = kotlin.math.floor(broadphase.maxY).toInt()
        val maxZ = kotlin.math.floor(broadphase.maxZ).toInt()
        val mutable = BlockPos.MutableBlockPos()
        val collisionContext = CollisionContext.of(boat)
        var nearest: ObstacleHit? = null

        for (x in minX..maxX) for (y in minY..maxY) for (z in minZ..maxZ) {
            mutable.set(x, y, z)
            val state = boat.level().getBlockState(mutable)
            if (state.isAir) continue
            val shape = state.getCollisionShape(boat.level(), mutable, collisionContext)
            if (shape.isEmpty) continue

            for (localBox in shape.toAabbs()) {
                val candidate = localBox.move(x.toDouble(), y.toDouble(), z.toDouble()).toCoreBox()
                val distance = sweep.hitDistance(candidate) ?: continue
                val topY = findObstacleTop(
                    boat,
                    mutable,
                    candidate,
                    collisionContext
                )
                if (
                    nearest == null || distance < nearest.distance ||
                    distance == nearest.distance && topY > nearest.topY
                ) {
                    nearest = ObstacleHit(mutable.immutable(), state, distance, topY)
                }
            }
        }
        return nearest
    }

    /**
     * Finds the top of the collision stack connected to the shape that the boat is approaching.
     */
    private fun findObstacleTop(
        boat: AbstractBoat,
        position: BlockPos,
        contact: AxisAlignedBox,
        collisionContext: CollisionContext
    ): Double {
        var top = contact.maxY
        var y = position.y
        val mutable = BlockPos.MutableBlockPos(position.x, y, position.z)

        while (true) {
            val state = boat.level().getBlockState(mutable)
            val shape = state.getCollisionShape(boat.level(), mutable, collisionContext)
            var extendedTop = top
            var changed: Boolean
            do {
                changed = false
                for (localBox in shape.toAabbs()) {
                    val box = localBox.move(position.x.toDouble(), y.toDouble(), position.z.toDouble()).toCoreBox()
                    if (
                        box.overlapsHorizontally(contact) &&
                        box.minY <= extendedTop + COLLISION_EPSILON &&
                        box.maxY > extendedTop + COLLISION_EPSILON
                    ) {
                        extendedTop = box.maxY
                        changed = true
                    }
                }
            } while (changed)

            if (extendedTop > top + COLLISION_EPSILON) {
                top = extendedTop
            }

            y++
            mutable.set(position.x, y, position.z)
            val nextState = boat.level().getBlockState(mutable)
            val nextShape = nextState.getCollisionShape(boat.level(), mutable, collisionContext)
            val continues = nextShape.toAabbs().any { localBox ->
                val box = localBox.move(position.x.toDouble(), y.toDouble(), position.z.toDouble()).toCoreBox()
                box.overlapsHorizontally(contact) &&
                    box.minY <= top + COLLISION_EPSILON &&
                    box.maxY > top + COLLISION_EPSILON
            }
            if (!continues) break
        }
        return top
    }

    private fun AABB.toCoreBox() = AxisAlignedBox(minX, minY, minZ, maxX, maxY, maxZ)

    private const val COLLISION_EPSILON = 1.0e-7
}
