package dev.nyon.bbm.network

import dev.nyon.bbm.movement.BoatMovementController
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.vehicle.boat.AbstractBoat
import java.util.WeakHashMap

object ManualJumpRequestHandler {
    private const val COOLDOWN_TICKS = 2L
    private val lastJumpTick = WeakHashMap<ServerPlayer, Long>()

    fun handle(player: ServerPlayer, payload: ManualJumpRequestPayload) {
        val boat = player.vehicle as? AbstractBoat ?: return
        if (boat.uuid != payload.boatId || boat.controllingPassenger !== player) return
        val now = player.level().gameTime
        val previous = lastJumpTick[player]
        if (previous != null && now - previous < COOLDOWN_TICKS) return
        if (BoatMovementController.tryManualJump(boat)) lastJumpTick[player] = now
    }
}
