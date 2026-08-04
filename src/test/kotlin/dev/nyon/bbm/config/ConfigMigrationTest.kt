package dev.nyon.bbm.config

import kotlinx.serialization.json.Json
import net.minecraft.world.entity.vehicle.boat.AbstractBoat.Status
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ConfigMigrationTest {
    @Test
    fun `version 2 fixture migrates`() {
        val migrated = assertNotNull(migrateConfig(Json.parseToJsonElement(
            """{"boostUnderwater":true,"playerEjectTicks":80.0,"onlyForPlayers":false}"""
        ), 2))
        assertEquals(80f, migrated.playerEjectTicks)
        assertFalse(migrated.boosting.onlyForPlayers)
        assertTrue(Status.UNDER_WATER in migrated.boosting.boostStates)
    }

    @Test
    fun `versions 3 and 4 fixtures migrate`() {
        for (version in 3..4) {
            val migrated = assertNotNull(migrateConfig(Json.parseToJsonElement(legacyBoostConfig()), version))
            assertEquals(0.7f, migrated.stepHeight)
            assertEquals(0.25, migrated.boosting.heightTolerance)
            assertEquals(setOf(Status.ON_LAND, Status.IN_WATER), migrated.boosting.boostStates)
            assertEquals(setOf(Identifier("#minecraft:ice")), migrated.boosting.allowedCollidingBlocks)
        }
    }

    @Test
    fun `versions 5 and 6 fixtures migrate keybind settings`() {
        for (version in 5..6) {
            val migrated = assertNotNull(migrateConfig(Json.parseToJsonElement(legacyBoostConfig(includeKeybind = true)), version))
            assertTrue(migrated.keybind.allowJumpKeybind)
            assertEquals(1.75, migrated.keybind.keybindJumpHeightMultiplier)
            assertFalse(migrated.keybind.onlyKeybindJumpOnGroundOrWater)
        }
    }

    @Test
    fun `unsupported migrations fail safely`() {
        assertNull(migrateConfig(Json.parseToJsonElement("{}"), 1))
        assertNull(migrateConfig(Json.parseToJsonElement("{}"), 99))
    }

    private fun legacyBoostConfig(includeKeybind: Boolean = false): String = """
        {
          "stepHeight": 0.7,
          "playerEjectTicks": 120.0,
          "boostUnderwater": false,
          "boostOnBlocks": true,
          "boostOnIce": true,
          "boostOnWater": true,
          "onlyForPlayers": true,
          "extraCollisionDetectionRange": 1.25
          ${if (includeKeybind) ",\"allowJumpKeybind\":true,\"keybindJumpHeightMultiplier\":1.75,\"onlyKeybindJumpOnGroundOrWater\":false" else ""}
        }
    """.trimIndent()
}
