package dev.nyon.bbm.extensions

import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerPlayer

fun sendToClient(player: ServerPlayer, packet: CustomPacketPayload) {
    //? if fabric
    net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.send(player, packet)
    //? if neoforge
    //net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(player, packet)
}

fun sendToServer(packet: CustomPacketPayload) {
    //? if fabric
    net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(packet)
    //? if neoforge
    //net.neoforged.neoforge.client.network.ClientPacketDistributor.sendToServer(packet)
}
