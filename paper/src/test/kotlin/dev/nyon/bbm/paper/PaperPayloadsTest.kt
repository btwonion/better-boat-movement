package dev.nyon.bbm.paper

import dev.nyon.bbm.paper.config.PaperConfigRepository
import dev.nyon.bbm.paper.config.PaperGameplayConfig
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.DataInputStream

class PaperPayloadsTest {
    @Test
    fun `default config uses the mod payload wire format`() {
        PaperConfigRepository.initialize(PaperGameplayConfig())
        val input = DataInputStream(ByteArrayInputStream(PaperPayloads().encodeConfig()))

        assertEquals(0.35f, input.readFloat())
        assertEquals(200f, input.readFloat())
        assertEquals(4, input.readVarInt())
        assertEquals(listOf(0, 1, 2, 3), List(4) { input.readVarInt() })
        assertEquals(0, input.readVarInt())
        assertEquals(0, input.readVarInt())
        assertEquals(true, input.readBoolean())
        assertEquals(0.5, input.readDouble())
        assertEquals(0.25, input.readDouble())
        assertEquals(false, input.readBoolean())
        assertEquals(1.2, input.readDouble())
        assertEquals(true, input.readBoolean())
        assertEquals(0, input.available())
    }

    private fun DataInputStream.readVarInt(): Int {
        var value = 0
        var position = 0
        while (position < 32) {
            val current = readUnsignedByte()
            value = value or ((current and 0x7F) shl position)
            if (current and 0x80 == 0) return value
            position += 7
        }
        error("VarInt is too large")
    }
}
