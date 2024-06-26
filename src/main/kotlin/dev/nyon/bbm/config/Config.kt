package dev.nyon.bbm.config

import dev.nyon.bbm.serverConfig
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.fabricmc.api.EnvType
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.Minecraft
import net.minecraft.network.FriendlyByteBuf
/*? if <1.20.5 {*/
import net.fabricmc.fabric.api.networking.v1.FabricPacket
import net.fabricmc.fabric.api.networking.v1.PacketType
import net.minecraft.resources.ResourceLocation

@Serializable
data class Config(
    var stepHeight: Float = 0.3f,
    var playerEjectTicks: Float = 20f * 10f,
    var boostUnderwater: Boolean = true,
    var wallHitCooldownTicks: Int = 5,
    var onlyForPlayers: Boolean = true
) : FabricPacket {
    companion object {
        @Transient
        val packetType: PacketType<Config> = PacketType.create(
            ResourceLocation("better-boat-movement", "sync")
        ) { buffer ->
            Config(buffer.readFloat(), buffer.readFloat(), buffer.readBoolean(), buffer.readInt(), buffer.readBoolean())
        }
    }

    override fun write(buffer: FriendlyByteBuf) {
        buffer.writeFloat(stepHeight)
        buffer.writeFloat(playerEjectTicks)
        buffer.writeBoolean(boostUnderwater)
        buffer.writeInt(wallHitCooldownTicks)
        buffer.writeBoolean(onlyForPlayers)
    }

    override fun getType(): PacketType<*> {
        return packetType
    }
}
/*?} else {*/
/*import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation

@Serializable
data class Config(
    var stepHeight: Float = 0.2f,
    var playerEjectTicks: Float = 20f * 10f,
    var boostUnderwater: Boolean = true,
    var wallHitCooldownTicks: Int = 5,
    var onlyForPlayers: Boolean = true
) : CustomPacketPayload {
    companion object {
        @Transient
        private val packetId = "better-boat-movement:sync"
        @Transient
        val packetType: CustomPacketPayload.Type<Config> =
            CustomPacketPayload.Type(/^? if >=1.21 {^/ /^ResourceLocation.parse(packetId)^//^?} else {^/ResourceLocation(packetId)/^?}^/)

        @Transient
        @Suppress("unused")
        val codec =
            object : StreamCodec<FriendlyByteBuf, Config> {
                override fun decode(buf: FriendlyByteBuf): Config {
                    return Config(
                        buf.readFloat(),
                        buf.readFloat(),
                        buf.readBoolean(),
                        buf.readInt(),
                        buf.readBoolean()
                    )
                }

                override fun encode(
                    buf: FriendlyByteBuf,
                    config: Config
                ) {
                    buf.writeFloat(config.stepHeight)
                    buf.writeFloat(config.playerEjectTicks)
                    buf.writeBoolean(config.boostUnderwater)
                    buf.writeInt(config.wallHitCooldownTicks)
                    buf.writeBoolean(config.onlyForPlayers)
                }
            }
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return packetType
    }
}
*//*?}*/

lateinit var config: Config

fun getActiveConfig(): Config? {
    if (FabricLoader.getInstance().environmentType == EnvType.SERVER) return config
    if (Minecraft.getInstance().isSingleplayer) return config
    return serverConfig
}
