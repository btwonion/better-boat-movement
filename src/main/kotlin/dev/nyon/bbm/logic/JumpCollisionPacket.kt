package dev.nyon.bbm.logic

import dev.nyon.bbm.extensions.resourceLocation
import kotlinx.serialization.Transient
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.player.Player
import java.util.UUID

data class JumpCollisionPacket(val id: /*? if >1.21.3 {*/ UUID /*?} else {*/ /*Int *//*?}*/) : CustomPacketPayload {
    companion object {
        @Transient
        val packetType: CustomPacketPayload.Type<JumpCollisionPacket> =
            CustomPacketPayload.Type(resourceLocation("bbm:jump_collision")!!)

        @Transient
        @Suppress("unused")
        val codec = object : StreamCodec<FriendlyByteBuf, JumpCollisionPacket> {
            override fun decode(buf: FriendlyByteBuf): JumpCollisionPacket {
                val id = /*? if >1.21.3 {*/ buf.readUUID() /*?} else {*/ /*buf.readInt() *//*?}*/
                return JumpCollisionPacket(id)
            }

            override fun encode(buf: FriendlyByteBuf, packet: JumpCollisionPacket) {
                /*? if >1.21.3 {*/ buf.writeUUID(packet.id) /*?} else {*/ /*buf.writeInt(packet.id) *//*?}*/
            }
        }
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return packetType
    }

    fun handle(player: Player) {
        val boat = player.level().getEntity(id) as? BbmBoat ?: return
        boat.jumpCollision = true
    }
}