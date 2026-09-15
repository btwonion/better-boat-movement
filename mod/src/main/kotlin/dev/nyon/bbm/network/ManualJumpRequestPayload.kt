package dev.nyon.bbm.network

import dev.nyon.bbm.extensions.identifier
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import java.util.UUID

data class ManualJumpRequestPayload(val boatId: UUID) : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE

    companion object {
        val TYPE = CustomPacketPayload.Type<ManualJumpRequestPayload>(identifier("bbm:manual_jump")!!)
        val CODEC = object : StreamCodec<FriendlyByteBuf, ManualJumpRequestPayload> {
            override fun decode(buf: FriendlyByteBuf) = ManualJumpRequestPayload(buf.readUUID())
            override fun encode(buf: FriendlyByteBuf, payload: ManualJumpRequestPayload) {
                buf.writeUUID(payload.boatId)
            }
        }
    }
}
