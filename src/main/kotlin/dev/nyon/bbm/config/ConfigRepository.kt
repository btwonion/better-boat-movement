package dev.nyon.bbm.config

import dev.nyon.konfig.config.saveConfig

object ConfigRepository {
    lateinit var localConfig: GameplayConfig
        private set

    @Volatile
    private var localSnapshot: GameplayConfigSnapshot? = null

    @Volatile
    var remoteSnapshot: GameplayConfigSnapshot? = null
        private set

    fun initialize(config: GameplayConfig) {
        localConfig = config
        refreshLocal()
    }

    fun refreshLocal() {
        localSnapshot = ConfigValidator.snapshot(localConfig)
    }

    fun snapshotFor(clientSide: Boolean): GameplayConfigSnapshot? =
        if (clientSide) remoteSnapshot else localSnapshot

    fun installRemote(snapshot: GameplayConfigSnapshot) {
        remoteSnapshot = snapshot
        reloadCache(snapshot)
    }

    fun clearRemote() {
        remoteSnapshot = null
    }

    fun save() {
        refreshLocal()
        saveConfig(localConfig)
        reloadCache(localSnapshot ?: return)
    }
}
