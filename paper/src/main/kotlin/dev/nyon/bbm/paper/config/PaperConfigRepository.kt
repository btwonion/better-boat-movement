package dev.nyon.bbm.paper.config

import java.util.concurrent.atomic.AtomicReference

object PaperConfigRepository {
    private val snapshotReference = AtomicReference<PaperGameplayConfigSnapshot>()

    val snapshot: PaperGameplayConfigSnapshot
        get() = snapshotReference.get() ?: error("BBM Paper configuration was requested before initialization")

    fun initialize(config: PaperGameplayConfig) {
        snapshotReference.set(config.toSnapshot())
    }
}
