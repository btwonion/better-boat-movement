package dev.nyon.bbm.config

import dev.nyon.konfig.config.saveConfig
import java.util.concurrent.atomic.AtomicReference

object ConfigRepository {
    lateinit var localConfig: GameplayConfig
        private set

    private val localSnapshot = AtomicReference<GameplayConfigSnapshot?>()

    private val remoteSnapshotReference = AtomicReference<GameplayConfigSnapshot?>()
    val remoteSnapshot: GameplayConfigSnapshot?
        get() = remoteSnapshotReference.get()

    fun initialize(config: GameplayConfig) {
        localConfig = config
        refreshLocal()
    }

    fun refreshLocal() {
        localSnapshot.set(localConfig.toSnapshot())
    }

    fun snapshotFor(clientSide: Boolean): GameplayConfigSnapshot? =
        if (clientSide) remoteSnapshotReference.get() else localSnapshot.get()

    fun installRemote(snapshot: GameplayConfigSnapshot) {
        remoteSnapshotReference.set(snapshot)
        reloadCache(snapshot)
    }

    fun clearRemote() {
        remoteSnapshotReference.set(null)
    }

    fun save() {
        refreshLocal()
        saveConfig(localConfig)
        reloadCache(localSnapshot.get() ?: return)
    }
}
