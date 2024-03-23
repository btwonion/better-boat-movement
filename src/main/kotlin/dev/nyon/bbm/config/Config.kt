package dev.nyon.bbm.config

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.fabricmc.fabric.api.networking.v1.FabricPacket
import net.fabricmc.fabric.api.networking.v1.PacketType
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation

@Serializable
data class Config(var groundStepHeight: Float = 0.25f, var waterStepHeight: Float = 3f, var playerEjectTicks: Float = 20f * 10f) :
    FabricPacket {
    companion object {
        @Transient
        val packetType: PacketType<Config> =
            PacketType.create(
                ResourceLocation("better-boat-movement", "sync")
            ) { buffer ->
                Config(buffer.readFloat(), buffer.readFloat(), buffer.readFloat())
            }
    }

    override fun write(buffer: FriendlyByteBuf) {
        buffer.writeFloat(groundStepHeight)
        buffer.writeFloat(waterStepHeight)
        buffer.writeFloat(playerEjectTicks)
    }

    override fun getType(): PacketType<*> {
        return packetType
    }
}
