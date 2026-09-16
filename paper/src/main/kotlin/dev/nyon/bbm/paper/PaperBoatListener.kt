package dev.nyon.bbm.paper

import dev.nyon.bbm.paper.config.PaperConfigRepository
import dev.nyon.bbm.paper.movement.PaperHorizontalMotion
import dev.nyon.bbm.paper.movement.PaperHorizontalMotionTracker
import dev.nyon.bbm.paper.movement.PaperMovementController
import org.bukkit.entity.Boat
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.vehicle.VehicleDestroyEvent
import org.bukkit.event.vehicle.VehicleExitEvent
import org.bukkit.event.vehicle.VehicleMoveEvent
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class PaperBoatListener : Listener {
    private val submergedTicks = ConcurrentHashMap<UUID, SubmergedState>()
    private val horizontalMotion = PaperHorizontalMotionTracker()

    @EventHandler(priority = EventPriority.NORMAL)
    fun onVehicleMove(event: VehicleMoveEvent) {
        val boat = event.vehicle as? Boat ?: return
        updateSubmergedTicks(boat)
        val movement = horizontalMotion.record(
            boat.uniqueId,
            boat.world.fullTime,
            PaperHorizontalMotion(event.to.x - event.from.x, event.to.z - event.from.z)
        )
        PaperMovementController.applyAutomaticBoost(boat, movement)
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onVehicleExit(event: VehicleExitEvent) {
        val boat = event.vehicle as? Boat ?: return
        val player = event.exited as? Player ?: return
        if (player.isSneaking || !boat.isUnderWater) return

        val ticks = submergedTicks[boat.uniqueId]?.ticks ?: 0f
        if (ticks < PaperConfigRepository.snapshot.playerEjectTicks) event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onVehicleExited(event: VehicleExitEvent) {
        horizontalMotion.remove(event.vehicle.uniqueId)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onVehicleDestroy(event: VehicleDestroyEvent) {
        submergedTicks.remove(event.vehicle.uniqueId)
        horizontalMotion.remove(event.vehicle.uniqueId)
    }

    private fun updateSubmergedTicks(boat: Boat) {
        if (!boat.isUnderWater) {
            submergedTicks.remove(boat.uniqueId)
            return
        }

        val now = boat.world.fullTime
        val state = submergedTicks.compute(boat.uniqueId) { _, current ->
            when {
                current == null -> SubmergedState(now, 1f)
                current.lastTick == now -> current
                else -> SubmergedState(now, current.ticks + (now - current.lastTick).coerceAtMost(20).toFloat())
            }
        } ?: return
        val threshold = PaperConfigRepository.snapshot.playerEjectTicks
        if (state.ticks >= threshold && boat.passengers.any { it is Player }) boat.eject()
    }

    private data class SubmergedState(val lastTick: Long, val ticks: Float)
}
