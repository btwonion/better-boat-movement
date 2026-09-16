package dev.nyon.bbm.paper

import dev.nyon.bbm.paper.config.PaperConfigRepository
import dev.nyon.bbm.paper.config.PaperGameplayConfigSnapshot
import dev.nyon.bbm.paper.movement.PaperMovementController
import org.bukkit.Bukkit
import org.bukkit.entity.Boat
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.nio.charset.StandardCharsets
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

const val CONFIG_CHANNEL = "bbm:config_snapshot"
const val MANUAL_JUMP_CHANNEL = "bbm:manual_jump"

class PaperPayloads : PluginMessageListener {
    private val lastManualJumpTick = ConcurrentHashMap<UUID, Int>()

    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray) {
        if (channel != MANUAL_JUMP_CHANNEL || message.size != UUID_BYTES) return
        val requestedBoatId = DataInputStream(ByteArrayInputStream(message)).use { input ->
            UUID(input.readLong(), input.readLong())
        }
        val boat = player.vehicle as? Boat ?: return
        if (boat.uniqueId != requestedBoatId || boat.passengers.firstOrNull() !== player) return

        val now = Bukkit.getCurrentTick()
        val previous = lastManualJumpTick[player.uniqueId]
        if (previous != null && now - previous < MANUAL_JUMP_COOLDOWN_TICKS) return
        if (PaperMovementController.tryManualJump(player, boat)) {
            lastManualJumpTick[player.uniqueId] = now
        }
    }

    fun forget(playerId: UUID) {
        lastManualJumpTick.remove(playerId)
    }

    fun clear() {
        lastManualJumpTick.clear()
    }

    fun encodeConfig(): ByteArray = encodeConfig(PaperConfigRepository.snapshot)

    private fun encodeConfig(config: PaperGameplayConfigSnapshot): ByteArray {
        val bytes = ByteArrayOutputStream()
        DataOutputStream(bytes).use { output ->
            output.writeFloat(config.stepHeight)
            output.writeFloat(config.playerEjectTicks)
            val statuses = config.boostStates.sortedBy { it.wireOrdinal }
            output.writeVarInt(statuses.size)
            statuses.forEach { output.writeVarInt(it.wireOrdinal) }
            output.writeIdentifiers(config.allowedSupportingBlocks.map { it.toString() }.sorted())
            output.writeIdentifiers(config.allowedCollidingBlocks.map { it.toString() }.sorted())
            output.writeBoolean(config.onlyForPlayers)
            output.writeDouble(config.extraCollisionDetectionRange)
            output.writeDouble(config.heightTolerance)
            output.writeBoolean(config.allowJumpKeybind)
            output.writeDouble(config.keybindJumpHeightMultiplier)
            output.writeBoolean(config.onlyKeybindJumpOnGroundOrWater)
        }
        return bytes.toByteArray()
    }

    private fun DataOutputStream.writeIdentifiers(identifiers: List<String>) {
        writeVarInt(identifiers.size)
        identifiers.forEach { identifier ->
            val bytes = identifier.toByteArray(StandardCharsets.UTF_8)
            writeVarInt(bytes.size)
            write(bytes)
        }
    }

    private fun DataOutputStream.writeVarInt(value: Int) {
        var remaining = value
        while ((remaining and 0x7F.inv()) != 0) {
            writeByte((remaining and 0x7F) or 0x80)
            remaining = remaining ushr 7
        }
        writeByte(remaining)
    }

    private companion object {
        const val UUID_BYTES = 16
        const val MANUAL_JUMP_COOLDOWN_TICKS = 2
    }
}
