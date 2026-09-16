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

    fun refreshLocal(syncRemote: Boolean = false): GameplayConfigSnapshot {
        val snapshot = localConfig.toSnapshot()
        localSnapshot.set(snapshot)
        if (syncRemote) remoteSnapshotReference.set(snapshot)
        return snapshot
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

    fun save(syncRemote: Boolean = false) {
        val snapshot = refreshLocal(syncRemote)
        saveConfig(localConfig)
        reloadCache(snapshot)
    }
}
