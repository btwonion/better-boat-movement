package dev.nyon.bbm.config

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ConfigValidatorTest {
    @Test
    fun `non-finite and out-of-range values are made safe`() {
        val config = GameplayConfig(
            stepHeight = Float.NaN,
            playerEjectTicks = -10f,
            boosting = GameplayConfig.Boosting(
                extraCollisionDetectionRange = Double.POSITIVE_INFINITY,
                heightTolerance = -1.0
            ),
            keybind = GameplayConfig.Keybind(keybindJumpHeightMultiplier = 100.0)
        )
        val snapshot = ConfigValidator.snapshot(config)
        assertEquals(0.35f, snapshot.stepHeight)
        assertEquals(0f, snapshot.playerEjectTicks)
        assertEquals(0.5, snapshot.extraCollisionDetectionRange)
        assertEquals(0.0, snapshot.heightTolerance)
        assertEquals(16.0, snapshot.keybindJumpHeightMultiplier)
    }

    @Test
    fun `invalid identifiers remain serializable and resolve safely`() {
        val invalid = Identifier("#not a valid id")
        assertEquals("#not a valid id", invalid.toString())
        assertNull(invalid.original)
    }
}
