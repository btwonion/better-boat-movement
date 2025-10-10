package dev.nyon.bbm.config

import dev.nyon.bbm.BetterBoatMovementEntrypoint
import dev.nyon.bbm.extensions.isClient
import dev.nyon.bbm.extensions.resourceLocation
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.*
import net.minecraft.network.FriendlyByteBuf
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
    var extraCollisionDetectionRange: Double = 0.5,
    var allowJumpKeybind: Boolean = false,
    var keybindJumpHeightMultiplier: Double = 1.5,
    var onlyKeybindJumpOnGroundOrWater: Boolean = true
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
                        buf.readDouble(),
                        buf.readBoolean(),
                        buf.readDouble(),
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
                    buf.writeDouble(config.extraCollisionDetectionRange)
                    buf.writeBoolean(config.allowJumpKeybind)
                    buf.writeDouble(config.keybindJumpHeightMultiplier)
                    buf.writeBoolean(config.onlyKeybindJumpOnGroundOrWater)
                }
            }
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return packetType
    }
}

lateinit var config: Config

val platform = /*? if fabric {*/
    net.fabricmc.loader.api.FabricLoader.getInstance().environmentType.toString().lowercase() /*?} else {*/
    /*BetterBoatMovementEntrypoint.dist.toString().lowercase() *//*?}*/

fun getActiveConfig(): Config? {
    if (platform.contains("server")) return config
    if (isClient && net.minecraft.client.Minecraft.getInstance().isSingleplayer) return config
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
        3, 4 -> Config(
            jsonObject["stepHeight"]?.jsonPrimitive?.floatOrNull ?: return null,
            jsonObject["playerEjectTicks"]?.jsonPrimitive?.floatOrNull ?: return null,
            jsonObject["boostUnderwater"]?.jsonPrimitive?.booleanOrNull ?: return null,
            jsonObject["boostOnBlocks"]?.jsonPrimitive?.booleanOrNull ?: return null,
            jsonObject["boostOnIce"]?.jsonPrimitive?.booleanOrNull ?: return null,
            jsonObject["boostOnWater"]?.jsonPrimitive?.booleanOrNull ?: return null,
            jsonObject["onlyForPlayers"]?.jsonPrimitive?.booleanOrNull ?: return null,
            jsonObject["extraCollisionDetectionRange"]?.jsonPrimitive?.doubleOrNull ?: return null
        )
        5 -> Config(
            jsonObject["stepHeight"]?.jsonPrimitive?.floatOrNull ?: return null,
            jsonObject["playerEjectTicks"]?.jsonPrimitive?.floatOrNull ?: return null,
            jsonObject["boostUnderwater"]?.jsonPrimitive?.booleanOrNull ?: return null,
            jsonObject["boostOnBlocks"]?.jsonPrimitive?.booleanOrNull ?: return null,
            jsonObject["boostOnIce"]?.jsonPrimitive?.booleanOrNull ?: return null,
            jsonObject["boostOnWater"]?.jsonPrimitive?.booleanOrNull ?: return null,
            jsonObject["onlyForPlayers"]?.jsonPrimitive?.booleanOrNull ?: return null,
            jsonObject["extraCollisionDetectionRange"]?.jsonPrimitive?.doubleOrNull ?: return null,
            jsonObject["allowJumpKeybind"]?.jsonPrimitive?.booleanOrNull ?: return null,
            jsonObject["keybindJumpHeightMultiplier"]?.jsonPrimitive?.doubleOrNull ?: return null
        )
        else -> null
    }
}

fun saveConfig() {
    dev.nyon.konfig.config.saveConfig(config)
}

var serverConfig: Config? = null