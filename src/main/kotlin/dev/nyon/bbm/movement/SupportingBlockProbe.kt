package dev.nyon.bbm.movement

import net.minecraft.core.BlockPos
import net.minecraft.world.entity.vehicle.boat.AbstractBoat
import net.minecraft.world.level.block.Block
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.shapes.CollisionContext

object SupportingBlockProbe {
    fun hasAllowedSupport(boat: AbstractBoat, allowedBlocks: Set<Block>): Boolean {
        if (allowedBlocks.isEmpty()) return true
        val box = boat.boundingBox
        val supportSlice = AABB(box.minX, box.minY - 0.01, box.minZ, box.maxX, box.minY + 0.001, box.maxZ)
        val min = BlockPos.containing(supportSlice.minX, supportSlice.minY, supportSlice.minZ)
        val max = BlockPos.containing(supportSlice.maxX, supportSlice.maxY, supportSlice.maxZ)
        val mutable = BlockPos.MutableBlockPos()
        val collisionContext = CollisionContext.of(boat)
        for (x in min.x..max.x) for (y in min.y..max.y) for (z in min.z..max.z) {
            mutable.set(x, y, z)
            val state = boat.level().getBlockState(mutable)
            if (state.block !in allowedBlocks) continue
            val shape = state.getCollisionShape(boat.level(), mutable, collisionContext)
            if (shape.toAabbs().any { it.move(x.toDouble(), y.toDouble(), z.toDouble()).intersects(supportSlice) }) {
                return true
            }
        }
        return false
    }
}
