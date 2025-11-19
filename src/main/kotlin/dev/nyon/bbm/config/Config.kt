package dev.nyon.bbm.config

import dev.nyon.bbm.extensions.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import java.util.*

@Serializable
data class Config(
    var stepHeight: Float = 0.35f,
    var playerEjectTicks: Float = 20f * 10f,
    var boosting: Boosting = Boosting(),
    var keybind: Keybind = Keybind()

) : CustomPacketPayload {
    @Serializable
    data class Boosting(
        var boostStates: MutableSet<Status> = mutableSetOf(
            Status.ON_LAND,
            Status.IN_WATER,
            Status.UNDER_FLOWING_WATER,
            Status.UNDER_WATER
        ),
        var allowedSupportingBlocks: MutableSet<Identifier> = mutableSetOf(),
        var allowedCollidingBlocks: MutableSet<Identifier> = mutableSetOf(),
        var onlyForPlayers: Boolean = true,
        var extraCollisionDetectionRange: Double = 0.5
    )

    @Serializable
    data class Keybind(
        var allowJumpKeybind: Boolean = false,
        var keybindJumpHeightMultiplier: Double = 1.5,
        var onlyKeybindJumpOnGroundOrWater: Boolean = true
    )

    companion object {
        @Transient
        val packetType: CustomPacketPayload.Type<Config> = CustomPacketPayload.Type(resourceLocation("bbm:sync")!!)

        @Transient
        @Suppress("unused")
        val codec = object : StreamCodec<FriendlyByteBuf, Config> {
            override fun decode(buf: FriendlyByteBuf): Config {
                return Config(
                    buf.readFloat(), buf.readFloat(), boosting = Boosting(
                        buf.readEnumSet(Status::class.java),
                        buf.readIdentifierSet(),
                        buf.readIdentifierSet(),
                        buf.readBoolean(),
                        buf.readDouble()
                    ), keybind = Keybind(
                        buf.readBoolean(), buf.readDouble(), buf.readBoolean()
                    )
                )
            }

            override fun encode(
                buf: FriendlyByteBuf, config: Config
            ) {
                buf.writeFloat(config.stepHeight)
                buf.writeFloat(config.playerEjectTicks)
                buf.writeEnumSet(EnumSet.copyOf(config.boosting.boostStates), Status::class.java)
                buf.writeIdentifierSet(config.boosting.allowedSupportingBlocks)
                buf.writeIdentifierSet(config.boosting.allowedCollidingBlocks)
                buf.writeBoolean(config.boosting.onlyForPlayers)
                buf.writeDouble(config.boosting.extraCollisionDetectionRange)
                buf.writeBoolean(config.keybind.allowJumpKeybind)
                buf.writeDouble(config.keybind.keybindJumpHeightMultiplier)
                buf.writeBoolean(config.keybind.onlyKeybindJumpOnGroundOrWater)
            }
        }
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return packetType
    }
}

lateinit var config: Config

val platform = /*? if fabric {*/
    /*net.fabricmc.loader.api.FabricLoader.getInstance().environmentType.toString()
        .lowercase() *//*?} else {*/dev.nyon.bbm.BetterBoatMovementEntrypoint.dist.toString().lowercase() /*?}*/

fun getActiveConfig(): Config? {
    if (platform.contains("server")) return config
    if (isClient && net.minecraft.client.Minecraft.getInstance().isSingleplayer) return config
    return serverConfig
}

fun saveConfig() {
    dev.nyon.konfig.config.saveConfig(config)
}

var serverConfig: Config? = null