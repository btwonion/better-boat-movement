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
import org.bukkit.event.vehicle.VehicleEnterEvent
import org.bukkit.event.vehicle.VehicleExitEvent
import org.bukkit.event.vehicle.VehicleMoveEvent
import org.bukkit.event.world.EntitiesUnloadEvent
import org.bukkit.plugin.Plugin
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class PaperBoatListener(private val plugin: Plugin) : Listener {
    private val submergedTicks = ConcurrentHashMap<UUID, Float>()
    private val trackedBoats = ConcurrentHashMap<UUID, Boat>()
    private val horizontalMotion = PaperHorizontalMotionTracker()

    @EventHandler(priority = EventPriority.NORMAL)
    fun onVehicleMove(event: VehicleMoveEvent) {
        val boat = event.vehicle as? Boat ?: return
        if (boat.passengers.any { it is Player }) trackSubmersion(boat)
        val movement = horizontalMotion.record(
            boat.uniqueId,
            boat.world.fullTime,
            PaperHorizontalMotion(event.to.x - event.from.x, event.to.z - event.from.z)
        )
        PaperMovementController.applyAutomaticBoost(boat, movement)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onVehicleEnter(event: VehicleEnterEvent) {
        val boat = event.vehicle as? Boat ?: return
        if (event.entered is Player) trackSubmersion(boat)
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun onVehicleExit(event: VehicleExitEvent) {
        val boat = event.vehicle as? Boat ?: return
        val player = event.exited as? Player ?: return
        if (player.isSneaking || !boat.isUnderWater) return

        val ticks = submergedTicks[boat.uniqueId] ?: 0f
        if (ticks < PaperConfigRepository.snapshot.playerEjectTicks) event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onVehicleExited(event: VehicleExitEvent) {
        horizontalMotion.remove(event.vehicle.uniqueId)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onVehicleDestroy(event: VehicleDestroyEvent) {
        clearBoat(event.vehicle.uniqueId)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun onEntitiesUnload(event: EntitiesUnloadEvent) {
        event.entities.filterIsInstance<Boat>().forEach { clearBoat(it.uniqueId) }
    }

    fun shutdown() {
        submergedTicks.clear()
        trackedBoats.clear()
        horizontalMotion.clear()
    }

    private fun trackSubmersion(boat: Boat) {
        val id = boat.uniqueId
        if (trackedBoats.putIfAbsent(id, boat) != null) return

        val task = boat.scheduler.runAtFixedRate(
            plugin,
            { scheduledTask ->
                if (boat.passengers.none { it is Player }) {
                    scheduledTask.cancel()
                    stopTrackingSubmersion(boat)
                } else {
                    updateSubmergedTicks(boat)
                }
            },
            { retireBoat(boat) },
            1L,
            1L
        )
        if (task == null) stopTrackingSubmersion(boat)
    }

    private fun updateSubmergedTicks(boat: Boat) {
        if (!boat.isUnderWater) {
            submergedTicks.remove(boat.uniqueId)
            return
        }

        val ticks = submergedTicks.compute(boat.uniqueId) { _, current -> (current ?: 0f) + 1f } ?: return
        val threshold = PaperConfigRepository.snapshot.playerEjectTicks
        if (ticks >= threshold) boat.eject()
    }

    private fun stopTrackingSubmersion(boat: Boat) {
        if (trackedBoats.remove(boat.uniqueId, boat)) submergedTicks.remove(boat.uniqueId)
    }

    private fun retireBoat(boat: Boat) {
        if (!trackedBoats.remove(boat.uniqueId, boat)) return
        submergedTicks.remove(boat.uniqueId)
        horizontalMotion.remove(boat.uniqueId)
    }

    private fun clearBoat(id: UUID) {
        trackedBoats.remove(id)
        submergedTicks.remove(id)
        horizontalMotion.remove(id)
    }
}
