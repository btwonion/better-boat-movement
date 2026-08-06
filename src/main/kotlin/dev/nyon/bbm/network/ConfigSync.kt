package dev.nyon.bbm.network

import dev.nyon.bbm.config.ConfigRepository

object ConfigSync {
    fun serverPayload(): ConfigSnapshotPayload = ConfigSnapshotPayload(
        ConfigRepository.snapshotFor(clientSide = false)
            ?: error("BBM configuration was requested before initialization")
    )

    fun receive(payload: ConfigSnapshotPayload) {
        ConfigRepository.installRemote(payload.snapshot)
    }

    fun disconnect() {
        ConfigRepository.clearRemote()
    }
}
