package dev.nyon.bbm.config

import kotlin.test.Test
import kotlin.test.assertEquals

class GameplayConfigTest {
    @Test
    fun `snapshot bounds finite numeric values`() {
        val snapshot = GameplayConfig(
            stepHeight = 100f,
            playerEjectTicks = -1f,
            boosting = GameplayConfig.Boosting(
                extraCollisionDetectionRange = Double.MAX_VALUE,
                heightTolerance = -1.0
            ),
            keybind = GameplayConfig.Keybind(
                keybindJumpHeightMultiplier = Double.POSITIVE_INFINITY
            )
        ).toSnapshot()

        assertEquals(GameplayConfigLimits.MAX_STEP_HEIGHT, snapshot.stepHeight)
        assertEquals(GameplayConfigLimits.MIN_PLAYER_EJECT_TICKS, snapshot.playerEjectTicks)
        assertEquals(
            GameplayConfigLimits.MAX_EXTRA_COLLISION_DETECTION_RANGE,
            snapshot.extraCollisionDetectionRange
        )
        assertEquals(GameplayConfigLimits.MIN_HEIGHT_TOLERANCE, snapshot.heightTolerance)
        assertEquals(
            GameplayConfigLimits.DEFAULT_KEYBIND_JUMP_HEIGHT_MULTIPLIER,
            snapshot.keybindJumpHeightMultiplier
        )
    }

    @Test
    fun `snapshot replaces non-finite local values with defaults`() {
        val snapshot = GameplayConfig(
            stepHeight = Float.NaN,
            playerEjectTicks = Float.NEGATIVE_INFINITY,
            boosting = GameplayConfig.Boosting(
                extraCollisionDetectionRange = Double.NaN,
                heightTolerance = Double.POSITIVE_INFINITY
            )
        ).toSnapshot()

        assertEquals(GameplayConfigLimits.DEFAULT_STEP_HEIGHT, snapshot.stepHeight)
        assertEquals(GameplayConfigLimits.DEFAULT_PLAYER_EJECT_TICKS, snapshot.playerEjectTicks)
        assertEquals(
            GameplayConfigLimits.DEFAULT_EXTRA_COLLISION_DETECTION_RANGE,
            snapshot.extraCollisionDetectionRange
        )
        assertEquals(GameplayConfigLimits.DEFAULT_HEIGHT_TOLERANCE, snapshot.heightTolerance)
    }

    @Test
    fun `refresh can synchronize the integrated client snapshot`() {
        ConfigRepository.initialize(GameplayConfig(stepHeight = 1.25f))

        val refreshed = ConfigRepository.refreshLocal(syncRemote = true)

        assertEquals(refreshed, ConfigRepository.snapshotFor(clientSide = false))
        assertEquals(refreshed, ConfigRepository.snapshotFor(clientSide = true))
        ConfigRepository.clearRemote()
    }
}
