package dev.nyon.bbm.config

import dev.nyon.bbm.extensions.resourceLocation
import dev.nyon.bbm.serverConfig
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.*
import net.fabricmc.api.EnvType
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.Minecraft
import net.minecraft.network.FriendlyByteBuf

/*? if <1.20.6 {*/
/*import net.fabricmc.fabric.api.networking.v1.FabricPacket
import net.fabricmc.fabric.api.networking.v1.PacketType

@Serializable
data class Config(
    var stepHeight: Float = 0.35f,
    var playerEjectTicks: Float = 20f * 10f,
    var boostUnderwater: Boolean = true,
    var boostOnBlocks: Boolean = true,
    var boostOnIce: Boolean = true,
    var boostOnWater: Boolean = true,
    var onlyForPlayers: Boolean = true
) : FabricPacket {
    companion object {
        @Transient
        val packetType: PacketType<Config> = PacketType.create(
            resourceLocation("better-boat-movement:sync")!!
        ) { buffer ->
            Config(
                buffer.readFloat(),
                buffer.readFloat(),
                buffer.readBoolean(),
                buffer.readBoolean(),
                buffer.readBoolean(),
                buffer.readBoolean(),
                buffer.readBoolean()
            )
        }
    }

    override fun write(buf: FriendlyByteBuf) {
        buf.writeFloat(stepHeight)
        buf.writeFloat(playerEjectTicks)
        buf.writeBoolean(boostUnderwater)
        buf.writeBoolean(boostOnBlocks)
        buf.writeBoolean(boostOnIce)
        buf.writeBoolean(boostOnWater)
        buf.writeBoolean(onlyForPlayers)
    }

    override fun getType(): PacketType<*> {
        return packetType
    }
}
*//*?} else {*/
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload

@Serializable
data class Config(
    var stepHeight: Float = 0.35f,
    var playerEjectTicks: Float = 20f * 10f,
    var boostUnderwater: Boolean = true,
    var boostOnBlocks: Boolean = true,
    var boostOnIce: Boolean = true,
    var boostOnWater: Boolean = true,
    var onlyForPlayers: Boolean = true
) : CustomPacketPayload {
    companion object {
        @Transient
        val packetType: CustomPacketPayload.Type<Config> = CustomPacketPayload.Type(resourceLocation("better-boat-movement:sync")!!)

        @Transient
        @Suppress("unused")
        val codec =
            object : StreamCodec<FriendlyByteBuf, Config> {
                override fun decode(buf: FriendlyByteBuf): Config {
                    return Config(
                        buf.readFloat(),
                        buf.readFloat(),
                        buf.readBoolean(),
                        buf.readBoolean(),
                        buf.readBoolean(),
                        buf.readBoolean(),
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
                    buf.writeBoolean(config.boostOnBlocks)
                    buf.writeBoolean(config.boostOnIce)
                    buf.writeBoolean(config.boostOnWater)
                    buf.writeBoolean(config.onlyForPlayers)
                }
            }
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return packetType
    }
}
/*?}*/

lateinit var config: Config

fun getActiveConfig(): Config? {
    if (FabricLoader.getInstance().environmentType == EnvType.SERVER) return config
    if (Minecraft.getInstance().isSingleplayer) return config
    return serverConfig
}

fun migrate(tree: JsonElement, version: Int?): Config? {
    val jsonObject = tree.jsonObject
    return when (version) {
        1 -> null
        2 -> Config(
            playerEjectTicks = jsonObject["playerEjectTicks"]?.jsonPrimitive?.floatOrNull ?: return null,
            boostUnderwater = jsonObject["boostUnderwater"]?.jsonPrimitive?.booleanOrNull ?: return null,
            onlyForPlayers = jsonObject["onlyForPlayers"]?.jsonPrimitive?.booleanOrNull ?: return null
        )
        else -> null
    }
}