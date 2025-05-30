package dev.nyon.bbm.config

import dev.nyon.bbm.extensions.resourceLocation
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.*
import net.minecraft.client.Minecraft
import net.minecraft.network.FriendlyByteBuf

/*? if >=1.20.5 {*/
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload

@Serializable
data class Config(
    var stepHeight: Float = 0.35f,
    var playerEjectTicks: Float = 20f * 10f,
    var boostUnderwater: Boolean = true,
    var boostOnBlocks: Boolean = true,
    var boostOnIce: Boolean = false,
    var boostOnWater: Boolean = true,
    var onlyForPlayers: Boolean = true,
    var extraCollisionDetectionRange: Double = 0.5
) : CustomPacketPayload {
    companion object {
        @Transient
        val packetType: CustomPacketPayload.Type<Config> = CustomPacketPayload.Type(resourceLocation("bbm:sync")!!)

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
                        buf.readBoolean(),
                        buf.readDouble()
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
                    buf.writeDouble(config.extraCollisionDetectionRange)
                }
            }
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return packetType
    }
}
/*?} else if fabric {*/
/*import net.fabricmc.fabric.api.networking.v1.FabricPacket
import net.fabricmc.fabric.api.networking.v1.PacketType

@Serializable
data class Config(
    var stepHeight: Float = 0.35f,
    var playerEjectTicks: Float = 20f * 10f,
    var boostUnderwater: Boolean = true,
    var boostOnBlocks: Boolean = true,
    var boostOnIce: Boolean = false,
    var boostOnWater: Boolean = true,
    var onlyForPlayers: Boolean = true,
    var extraCollisionDetectionRange: Double = 0.5
) : FabricPacket {
    companion object {
        @Transient
        val packetType: PacketType<Config> = PacketType.create(
            resourceLocation("bbm:sync")!!
        ) { buffer ->
            Config(
                buffer.readFloat(),
                buffer.readFloat(),
                buffer.readBoolean(),
                buffer.readBoolean(),
                buffer.readBoolean(),
                buffer.readBoolean(),
                buffer.readBoolean(),
                buffer.readDouble()
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
        buf.writeDouble(config.extraCollisionDetectionRange)
    }

    override fun getType(): PacketType<*> {
        return packetType
    }
}
*//*?} else if neoforge {*/
/*import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation

@Serializable
data class Config(
    var stepHeight: Float = 0.35f,
    var playerEjectTicks: Float = 20f * 10f,
    var boostUnderwater: Boolean = true,
    var boostOnBlocks: Boolean = true,
    var boostOnIce: Boolean = false,
    var boostOnWater: Boolean = true,
    var onlyForPlayers: Boolean = true,
    var extraCollisionDetectionRange: Double = 0.5
) : CustomPacketPayload {
    companion object {
        @Transient
        val identifier = resourceLocation("bbm:sync")!!
    }

    constructor(buf: FriendlyByteBuf) : this(
        buf.readFloat(),
        buf.readFloat(),
        buf.readBoolean(),
        buf.readBoolean(),
        buf.readBoolean(),
        buf.readBoolean(),
        buf.readBoolean(),
        buf.readDouble()
    )

    override fun write(buf: FriendlyByteBuf) {
        buf.writeFloat(config.stepHeight)
        buf.writeFloat(config.playerEjectTicks)
        buf.writeBoolean(config.boostUnderwater)
        buf.writeBoolean(config.boostOnBlocks)
        buf.writeBoolean(config.boostOnIce)
        buf.writeBoolean(config.boostOnWater)
        buf.writeBoolean(config.onlyForPlayers)
        buf.writeDouble(config.extraCollisionDetectionRange)
    }

    override fun id(): ResourceLocation {
        return identifier
    }
}*//*?} else {*/
/*@Serializable
data class Config(
    var stepHeight: Float = 0.35f,
    var playerEjectTicks: Float = 20f * 10f,
    var boostUnderwater: Boolean = true,
    var boostOnBlocks: Boolean = true,
    var boostOnIce: Boolean = false,
    var boostOnWater: Boolean = true,
    var onlyForPlayers: Boolean = true,
    var extraCollisionDetectionRange: Double = 0.5
) {
    constructor(buf: FriendlyByteBuf) : this(
        buf.readFloat(),
        buf.readFloat(),
        buf.readBoolean(),
        buf.readBoolean(),
        buf.readBoolean(),
        buf.readBoolean(),
        buf.readBoolean(),
        buf.readDouble()
    )

    fun write(buf: FriendlyByteBuf) {
        buf.writeFloat(config.stepHeight)
        buf.writeFloat(config.playerEjectTicks)
        buf.writeBoolean(config.boostUnderwater)
        buf.writeBoolean(config.boostOnBlocks)
        buf.writeBoolean(config.boostOnIce)
        buf.writeBoolean(config.boostOnWater)
        buf.writeBoolean(config.onlyForPlayers)
        buf.writeDouble(config.extraCollisionDetectionRange)
    }
}

*//*?}*/

lateinit var config: Config

val platform = /*? if fabric {*/
    net.fabricmc.loader.api.FabricLoader.getInstance().environmentType.toString().lowercase() /*?} else if forge {*/
    /*net.minecraftforge.fml.loading.FMLLoader.getDist().toString().lowercase() *//*?} else {*/
    /*net.neoforged.fml.loading.FMLLoader.getDist().toString().lowercase() *//*?}*/

fun getActiveConfig(): Config? {
    if (platform.contains("server")) return config
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
        3 -> Config(
            jsonObject["stepHeight"]?.jsonPrimitive?.floatOrNull ?: return null,
            jsonObject["playerEjectTicks"]?.jsonPrimitive?.floatOrNull ?: return null,
            jsonObject["boostUnderwater"]?.jsonPrimitive?.booleanOrNull ?: return null,
            jsonObject["boostOnBlocks"]?.jsonPrimitive?.booleanOrNull ?: return null,
            jsonObject["boostOnIce"]?.jsonPrimitive?.booleanOrNull ?: return null,
            jsonObject["boostOnWater"]?.jsonPrimitive?.booleanOrNull ?: return null,
            jsonObject["onlyForPlayers"]?.jsonPrimitive?.booleanOrNull ?: return null,
            jsonObject["extraCollisionDetectionRange"]?.jsonPrimitive?.doubleOrNull ?: return null
        )
        else -> null
    }
}

fun saveConfig() {
    dev.nyon.konfig.config.saveConfig(config)
}

var serverConfig: Config? = null