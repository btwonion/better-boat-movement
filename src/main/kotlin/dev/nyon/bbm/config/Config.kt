package dev.nyon.bbm.config

import dev.nyon.bbm.serverConfig
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.fabricmc.api.EnvType
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.Minecraft
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload

lateinit var config: Config

@Serializable
data class Config(
    var stepHeight: Float = 0.3f,
    var playerEjectTicks: Float = 20f * 10f,
    var boostUnderwater: Boolean = true,
    var wallHitCooldownTicks: Int = 5,
    var onlyForPlayers: Boolean = true
) : CustomPacketPayload {
    companion object {
        @Transient
        val packetType: CustomPacketPayload.Type<Config> =
            CustomPacketPayload.createType("better-boat-movement:sync")

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

fun getActiveConfig(): Config? {
    if (FabricLoader.getInstance().environmentType == EnvType.SERVER) return config
    if (Minecraft.getInstance().isSingleplayer) return config
    return serverConfig
}
