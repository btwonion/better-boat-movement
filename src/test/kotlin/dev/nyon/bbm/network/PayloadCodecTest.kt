package dev.nyon.bbm.network

import dev.nyon.bbm.config.GameplayConfig
import dev.nyon.bbm.config.ConfigValidator
import dev.nyon.bbm.config.Identifier
import io.netty.buffer.Unpooled
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.vehicle.boat.AbstractBoat.Status
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

class PayloadCodecTest {
    @Test
    fun `configuration snapshot round trips independently of persistence version`() {
        val snapshot = ConfigValidator.snapshot(
            GameplayConfig(
                stepHeight = 0.8f,
                boosting = GameplayConfig.Boosting(
                    boostStates = mutableSetOf(Status.ON_LAND),
                    allowedCollidingBlocks = mutableSetOf(Identifier("#minecraft:ice")),
                    heightTolerance = 0.4
                )
            )
        )
        val buffer = FriendlyByteBuf(Unpooled.buffer())
        ConfigSnapshotPayload.CODEC.encode(buffer, ConfigSnapshotPayload(snapshot))
        assertEquals(snapshot, ConfigSnapshotPayload.CODEC.decode(buffer).snapshot)
    }

    @Test
    fun `manual jump request round trips boat identity`() {
        val id = UUID.fromString("d34db33f-0000-4000-8000-000000000001")
        val buffer = FriendlyByteBuf(Unpooled.buffer())
        ManualJumpRequestPayload.CODEC.encode(buffer, ManualJumpRequestPayload(id))
        assertEquals(id, ManualJumpRequestPayload.CODEC.decode(buffer).boatId)
    }
}
