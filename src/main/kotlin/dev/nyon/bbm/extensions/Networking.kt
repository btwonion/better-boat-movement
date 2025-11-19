package dev.nyon.bbm.extensions

import dev.nyon.bbm.config.Identifier
import dev.nyon.bbm.config.IdentifierSerializer
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerPlayer

fun sendToClient(player: ServerPlayer, packet: CustomPacketPayload) {
    //? if fabric
    net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.send(player, packet)
    //? if neoforge
    /*net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(player, packet)*/
}

fun FriendlyByteBuf.writeIdentifierSet(set: MutableSet<Identifier>) {
    writeCollection(set) { buf, identifier ->
        buf.writeByteArray(identifier.toString().encodeToByteArray())
    }
}

fun FriendlyByteBuf.readIdentifierSet(): MutableSet<Identifier> {
    return readCollection(HashSet<Identifier>::newHashSet) { buf ->
        val string = buf.readByteArray().decodeToString()
        return@readCollection IdentifierSerializer.decodeFromString(string)
    }
}