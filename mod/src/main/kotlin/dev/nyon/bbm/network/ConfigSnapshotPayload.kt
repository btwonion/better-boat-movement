package dev.nyon.bbm.network

import dev.nyon.bbm.config.GameplayConfigSnapshot
import dev.nyon.bbm.config.Identifier
import dev.nyon.bbm.config.IdentifierSerializer
import dev.nyon.bbm.extensions.identifier
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
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
                val states = readCollection(buf) { it.readEnum(Status::class.java) }.toSet()
                val supporting = readCollection(buf, ::readIdentifier).toSet()
                val colliding = readCollection(buf, ::readIdentifier).toSet()
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
                writeCollection(buf, config.boostStates) { target, status -> target.writeEnum(status) }
                writeCollection(buf, config.allowedSupportingBlocks, ::writeIdentifier)
                writeCollection(buf, config.allowedCollidingBlocks, ::writeIdentifier)
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

            private fun <T> readCollection(
                buf: FriendlyByteBuf,
                decoder: (FriendlyByteBuf) -> T
            ): List<T> {
                val size = ByteBufCodecs.readCount(buf, ByteBufCodecs.MAX_INITIAL_COLLECTION_SIZE)
                return List(size) { decoder(buf) }
            }

            private fun <T> writeCollection(
                buf: FriendlyByteBuf,
                values: Collection<T>,
                encoder: (FriendlyByteBuf, T) -> Unit
            ) {
                ByteBufCodecs.writeCount(buf, values.size, ByteBufCodecs.MAX_INITIAL_COLLECTION_SIZE)
                values.forEach { encoder(buf, it) }
            }
        }
    }
}
