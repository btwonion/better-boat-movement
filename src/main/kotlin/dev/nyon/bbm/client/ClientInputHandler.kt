package dev.nyon.bbm.client

import dev.nyon.bbm.KeyBindings
import dev.nyon.bbm.extensions.sendToServer
import dev.nyon.bbm.movement.BoatMovementController
import dev.nyon.bbm.network.ManualJumpRequestPayload
import net.minecraft.client.Minecraft
import net.minecraft.world.entity.vehicle.boat.AbstractBoat

object ClientInputHandler {
    fun tick(minecraft: Minecraft) {
        while (KeyBindings.jumpKeyBind.consumeClick()) {
            val player = minecraft.player ?: continue
            val boat = player.vehicle as? AbstractBoat ?: continue
            if (boat.controllingPassenger !== player) continue
            if (!BoatMovementController.tryManualJump(boat)) continue
            sendToServer(ManualJumpRequestPayload(boat.uuid))
        }
    }
}
