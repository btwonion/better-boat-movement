package dev.nyon.bbm

import dev.nyon.bbm.config.getActiveConfig
import dev.nyon.bbm.extensions.resourceLocation
import kotlinx.serialization.Transient
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.Vec3

typealias ThisIsMyBoat = /*? if >=1.21.3 {*/ net.minecraft.world.entity.vehicle.AbstractBoat /*?} else {*/ /*net.minecraft.world.entity.vehicle.Boat *//*?}*/

object PressJumpKeybindingPacket : CustomPacketPayload {
    @Transient
    val packetType: CustomPacketPayload.Type<PressJumpKeybindingPacket> =
        CustomPacketPayload.Type(resourceLocation("bbm:jump")!!)

    @Transient
    @Suppress("unused")
    val codec = object : StreamCodec<FriendlyByteBuf, PressJumpKeybindingPacket> {
        override fun decode(buf: FriendlyByteBuf): PressJumpKeybindingPacket {
            return PressJumpKeybindingPacket
        }

        override fun encode(
            buf: FriendlyByteBuf, config: PressJumpKeybindingPacket
        ) {}
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return packetType
    }

    fun handlePacket(player: Player) {
        if (getActiveConfig()?.allowJumpKeybind == false) return
        val vehicle = player.vehicle ?: return
        val boat = vehicle as? ThisIsMyBoat ?: return
        val config = getActiveConfig() ?: return
        if (getActiveConfig()?.onlyKeybindJumpOnGroundOrWater == true && !boat.onGround() && !boat.isInWater && !boat.isUnderWater) return
        boat.addDeltaMovement(Vec3(0.0, config.stepHeight.toDouble() * config.keybindJumpHeightMultiplier, 0.0))
    }
}