package dev.nyon.bbm.config.screen

import kotlin.test.Test
import kotlin.test.assertEquals

class ConfigScreenTest {
    @Test
    fun `invalid filter entries are ignored`() {
        val identifiers = decodeIdentifiers(
            listOf("minecraft:stone", "not a valid id", "#minecraft:ice")
        )

        assertEquals(
            setOf("minecraft:stone", "#minecraft:ice"),
            identifiers.mapTo(mutableSetOf()) { it.toString() }
        )
    }
}
