package dev.nyon.bbm.paper.movement

import dev.nyon.bbm.paper.config.PaperBoatStatus
import dev.nyon.bbm.paper.config.PaperConfigRepository
import dev.nyon.bbm.paper.config.PaperIdentifier
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.block.Block
import org.bukkit.entity.Boat
import org.bukkit.entity.Player
import org.bukkit.util.BoundingBox
import org.bukkit.util.Vector
import kotlin.math.floor

object PaperMovementController {
    fun applyAutomaticBoost(boat: Boat) {
        val config = PaperConfigRepository.snapshot
        val status = boat.status.toPaperStatus() ?: return
        val obstacle = PaperObstacleProbe.findAhead(boat, config) ?: return
        if (config.extraCollisionDetectionRange == 0.0 &&
            obstacle.distance > PaperObstacleProbe.CONTACT_PROBE_RANGE
        ) return
        if (status !in config.boostStates) return
        if (config.onlyForPlayers && boat.passengers.none { it is Player }) return
        if (status == PaperBoatStatus.ON_LAND &&
            !hasAllowedSupport(boat, config.allowedSupportingBlocks)
        ) return
        if (!matches(obstacle.block, config.allowedCollidingBlocks)) return

        val box = boat.boundingBox
        val maximumRise = maximumRise(
            status,
            config.stepHeight.toDouble(),
            waterSurfaceY(boat),
            box.minY,
            box.height
        )
        if (obstacle.topY - box.minY > maximumRise + config.heightTolerance) return

        val velocity = boat.velocity
        boat.velocity = Vector(velocity.x, config.stepHeight.toDouble(), velocity.z)
    }

    fun tryManualJump(player: Player, boat: Boat): Boolean {
        val config = PaperConfigRepository.snapshot
        if (!config.allowJumpKeybind) return false
        if (boat.passengers.firstOrNull() !== player) return false
        if (config.onlyKeybindJumpOnGroundOrWater &&
            !boat.isOnGround && !boat.isInWater && !boat.isUnderWater
        ) return false

        val velocity = boat.velocity
        boat.velocity = velocity.add(Vector(0.0, config.stepHeight * config.keybindJumpHeightMultiplier, 0.0))
        return true
    }

    private fun hasAllowedSupport(boat: Boat, allowed: Set<PaperIdentifier>): Boolean {
        if (allowed.isEmpty()) return true
        val box = boat.boundingBox
        val slice = BoundingBox(box.minX, box.minY - 0.01, box.minZ, box.maxX, box.minY + 0.001, box.maxZ)
        val sliceCore = slice.toCoreBox()
        for (x in floor(slice.minX).toInt()..floor(slice.maxX).toInt()) {
            for (y in floor(slice.minY).toInt()..floor(slice.maxY).toInt()) {
                for (z in floor(slice.minZ).toInt()..floor(slice.maxZ).toInt()) {
                    val block = boat.world.getBlockAt(x, y, z)
                    if (!matches(block, allowed)) continue
                    if (block.collisionShape.boundingBoxes.any {
                        it.toCoreBox(x.toDouble(), y.toDouble(), z.toDouble()).intersects(sliceCore)
                    }) return true
                }
            }
        }
        return false
    }

    private fun matches(block: Block, allowed: Set<PaperIdentifier>): Boolean {
        if (allowed.isEmpty()) return true
        return allowed.any { identifier ->
            if (identifier.isTag) {
                Bukkit.getTag(Tag.REGISTRY_BLOCKS, identifier.key, Material::class.java)
                    ?.isTagged(block.type) == true
            } else {
                block.type.key == identifier.key
            }
        }
    }

    private fun waterSurfaceY(boat: Boat): Double {
        if (!boat.isInWater && !boat.isUnderWater) return boat.boundingBox.minY
        val location = boat.location
        var y = floor(boat.boundingBox.minY).toInt()
        val maxY = boat.world.maxHeight - 1
        while (y < maxY && boat.world.getBlockAt(location.blockX, y, location.blockZ).type == Material.WATER) y++
        return y.toDouble()
    }

    private fun maximumRise(
        initialStatus: PaperBoatStatus,
        initialVelocity: Double,
        waterSurfaceY: Double,
        initialY: Double,
        boatHeight: Double
    ): Double {
        if (initialVelocity <= 0.0 || !initialVelocity.isFinite()) return 0.0
        var status = initialStatus
        var velocity = initialVelocity
        var y = initialY
        var rise = 0.0
        repeat(MAX_ASCENT_TICKS) {
            var acceleration = -BOAT_GRAVITY
            val buoyancy = when (status) {
                PaperBoatStatus.IN_WATER -> if (boatHeight > 0.0) (waterSurfaceY - y) / boatHeight else 0.0
                PaperBoatStatus.UNDER_FLOWING_WATER -> {
                    acceleration = -FLOWING_WATER_ACCELERATION
                    0.0
                }
                PaperBoatStatus.UNDER_WATER -> UNDERWATER_BUOYANCY
                else -> 0.0
            }
            velocity += acceleration
            if (buoyancy > 0.0) velocity = (velocity + buoyancy * (BOAT_GRAVITY / BUOYANCY_DIVISOR)) * BUOYANCY_DAMPING
            if (!velocity.isFinite()) return Double.POSITIVE_INFINITY
            if (velocity <= 0.0) return rise
            y += velocity
            rise += velocity
            status = when {
                initialStatus !in WATER_STATUSES -> PaperBoatStatus.IN_AIR
                y >= waterSurfaceY -> PaperBoatStatus.IN_AIR
                y + boatHeight + SUBMERGED_EPSILON >= waterSurfaceY -> PaperBoatStatus.IN_WATER
                initialStatus == PaperBoatStatus.UNDER_FLOWING_WATER -> PaperBoatStatus.UNDER_FLOWING_WATER
                else -> PaperBoatStatus.UNDER_WATER
            }
        }
        return Double.POSITIVE_INFINITY
    }

    private fun Boat.Status.toPaperStatus(): PaperBoatStatus? = when (this) {
        Boat.Status.IN_WATER -> PaperBoatStatus.IN_WATER
        Boat.Status.UNDER_WATER -> PaperBoatStatus.UNDER_WATER
        Boat.Status.UNDER_FLOWING_WATER -> PaperBoatStatus.UNDER_FLOWING_WATER
        Boat.Status.ON_LAND -> PaperBoatStatus.ON_LAND
        Boat.Status.IN_AIR -> PaperBoatStatus.IN_AIR
        Boat.Status.NOT_IN_WORLD -> null
    }

    private val WATER_STATUSES = setOf(
        PaperBoatStatus.IN_WATER,
        PaperBoatStatus.UNDER_WATER,
        PaperBoatStatus.UNDER_FLOWING_WATER
    )
    private const val BOAT_GRAVITY = 0.04
    private const val FLOWING_WATER_ACCELERATION = 7.0e-4
    private const val UNDERWATER_BUOYANCY = 0.01
    private const val BUOYANCY_DIVISOR = 0.65
    private const val BUOYANCY_DAMPING = 0.75
    private const val SUBMERGED_EPSILON = 0.001
    private const val MAX_ASCENT_TICKS = 10_000
}
