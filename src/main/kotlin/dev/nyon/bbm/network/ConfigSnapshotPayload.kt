package dev.nyon.bbm.network

import dev.nyon.bbm.config.GameplayConfigSnapshot
import dev.nyon.bbm.config.Identifier
import dev.nyon.bbm.config.IdentifierSerializer
import dev.nyon.bbm.extensions.identifier
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.vehicle.boat.AbstractBoat.Status

data class ConfigSnapshotPayload(val snapshot: GameplayConfigSnapshot) : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE

    companion object {
        val TYPE = CustomPacketPayload.Type<ConfigSnapshotPayload>(identifier("bbm:config_snapshot")!!)
        val CODEC = object : StreamCodec<FriendlyByteBuf, ConfigSnapshotPayload> {
            override fun decode(buf: FriendlyByteBuf): ConfigSnapshotPayload {
                val stepHeight = buf.readFloat()
                val ejectTicks = buf.readFloat()
                val states = buf.readList { it.readEnum(Status::class.java) }.toSet()
                val supporting = buf.readList { readIdentifier(it) }.toSet()
                val colliding = buf.readList { readIdentifier(it) }.toSet()
                return ConfigSnapshotPayload(
                    GameplayConfigSnapshot(
                        stepHeight,
                        ejectTicks,
                        states,
                        supporting,
                        colliding,
                        buf.readBoolean(),
                        buf.readDouble(),
                        buf.readDouble(),
                        buf.readBoolean(),
                        buf.readDouble(),
                        buf.readBoolean()
                    )
                )
            }

            override fun encode(buf: FriendlyByteBuf, payload: ConfigSnapshotPayload) {
                val config = payload.snapshot
                buf.writeFloat(config.stepHeight)
                buf.writeFloat(config.playerEjectTicks)
                buf.writeCollection(config.boostStates) { target, status -> target.writeEnum(status) }
                buf.writeCollection(config.allowedSupportingBlocks) { target, id -> writeIdentifier(target, id) }
                buf.writeCollection(config.allowedCollidingBlocks) { target, id -> writeIdentifier(target, id) }
                buf.writeBoolean(config.onlyForPlayers)
                buf.writeDouble(config.extraCollisionDetectionRange)
                buf.writeDouble(config.heightTolerance)
                buf.writeBoolean(config.allowJumpKeybind)
                buf.writeDouble(config.keybindJumpHeightMultiplier)
                buf.writeBoolean(config.onlyKeybindJumpOnGroundOrWater)
            }

            private fun readIdentifier(buf: FriendlyByteBuf): Identifier =
                IdentifierSerializer.decodeFromString(buf.readUtf())

            private fun writeIdentifier(buf: FriendlyByteBuf, identifier: Identifier) =
                buf.writeUtf(identifier.toString())
        }
    }
}
