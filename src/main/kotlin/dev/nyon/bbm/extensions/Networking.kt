package dev.nyon.bbm.extensions

import dev.nyon.bbm.config.serverConfig
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerPlayer

fun sendToClient(player: ServerPlayer, packet: CustomPacketPayload) {
    //? if fabric
    net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.send(player, packet)
    //? if neoforge
    /*net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(player, serverConfig!!)*/
}