package dev.nyon.bbm.movement

import net.minecraft.core.BlockPos
import net.minecraft.tags.FluidTags
import net.minecraft.util.Mth
import net.minecraft.world.entity.vehicle.boat.AbstractBoat

/** Finds the top of the connected water column intersecting a submerged boat's hull. */
object WaterSurfaceProbe {
    fun findAbove(boat: AbstractBoat, fallback: Double): Double {
        val level = boat.level()
        val box = boat.boundingBox
        val startY = Mth.floor(box.maxY + SUBMERGED_EPSILON)
        var highestSurface = Double.NEGATIVE_INFINITY
        val pos = BlockPos.MutableBlockPos()

        for (x in Mth.floor(box.minX) until Mth.ceil(box.maxX)) {
            for (z in Mth.floor(box.minZ) until Mth.ceil(box.maxZ)) {
                var y = startY
                var foundWater = false
                while (y < level.maxY) {
                    pos.set(x, y, z)
                    val fluid = level.getFluidState(pos)
                    if (!fluid.`is`(FluidTags.WATER)) {
                        if (foundWater) highestSurface = maxOf(highestSurface, y.toDouble())
                        break
                    }

                    foundWater = true
                    val height = fluid.getHeight(level, pos).toDouble()
                    if (height < FULL_FLUID_HEIGHT) {
                        highestSurface = maxOf(highestSurface, y + height)
                        break
                    }
                    y++
                }

                if (foundWater && y >= level.maxY) {
                    highestSurface = maxOf(highestSurface, level.maxY.toDouble())
                }
            }
        }

        return if (highestSurface.isFinite()) highestSurface else fallback
    }

    private const val SUBMERGED_EPSILON = 0.001
    private const val FULL_FLUID_HEIGHT = 1.0
}
