package dev.nyon.bbm.config

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.fabricmc.fabric.api.networking.v1.FabricPacket
import net.fabricmc.fabric.api.networking.v1.PacketType
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation

lateinit var config: Config

@Serializable
data class Config(
    var stepHeight: Float = 0.3f,
    var playerEjectTicks: Float = 20f * 10f,
    var boostUnderwater: Boolean = true,
    var wallHitCooldownTicks: Int = 5,
    var onlyForPlayers: Boolean = true
) :
    FabricPacket {
    companion object {
        @Transient
        val packetType: PacketType<Config> =
            PacketType.create(
                ResourceLocation("better-boat-movement", "sync")
            ) { buffer ->
                Config(buffer.readFloat(), buffer.readFloat(), buffer.readBoolean(), buffer.readInt())
            }
    }

    override fun write(buffer: FriendlyByteBuf) {
        buffer.writeFloat(stepHeight)
        buffer.writeFloat(playerEjectTicks)
        buffer.writeBoolean(boostUnderwater)
        buffer.writeInt(wallHitCooldownTicks)
    }

    override fun getType(): PacketType<*> {
        return packetType
    }
}
