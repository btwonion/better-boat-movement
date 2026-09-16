package dev.nyon.bbm

import dev.nyon.bbm.config.ConfigRepository
import dev.nyon.bbm.config.GameplayConfig
import dev.nyon.bbm.config.migrateConfig
import dev.nyon.konfig.config.config
import dev.nyon.konfig.config.loadConfig
import java.nio.file.Path

internal fun bootstrap(path: Path) {
    config(path, 7, GameplayConfig()) { _, element, version -> migrateConfig(element, version) }
    ConfigRepository.initialize(loadConfig())
}
