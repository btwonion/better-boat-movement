package dev.nyon.bbm.paper

import dev.nyon.bbm.paper.config.PaperConfigRepository
import dev.nyon.bbm.paper.config.PaperGameplayConfig
import dev.nyon.bbm.paper.config.migratePaperConfig
import dev.nyon.konfig.config.config
import dev.nyon.konfig.config.loadConfig
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRegisterChannelEvent
import org.bukkit.plugin.java.JavaPlugin

class PaperEntrypoint : JavaPlugin(), Listener {
    private val payloads = PaperPayloads()
    private val boatListener = PaperBoatListener(this)

    override fun onLoad() {
        val path = Bukkit.getPluginsFolder().toPath()
            .resolve("better-boat-movement/better-boat-movement.json")
        config(path, 7, PaperGameplayConfig()) { _, element, version ->
            migratePaperConfig(element, version)
        }
        PaperConfigRepository.initialize(loadConfig())
    }

    override fun onEnable() {
        server.pluginManager.registerEvents(this, this)
        server.pluginManager.registerEvents(boatListener, this)
        server.messenger.registerOutgoingPluginChannel(this, CONFIG_CHANNEL)
        server.messenger.registerIncomingPluginChannel(this, MANUAL_JUMP_CHANNEL, payloads)
    }

    override fun onDisable() {
        boatListener.shutdown()
        payloads.clear()
        server.messenger.unregisterOutgoingPluginChannel(this)
        server.messenger.unregisterIncomingPluginChannel(this)
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        sendConfig(event.player)
    }

    @EventHandler
    fun onPlayerRegisterChannel(event: PlayerRegisterChannelEvent) {
        if (event.channel == CONFIG_CHANNEL || event.channel == MANUAL_JUMP_CHANNEL) {
            sendConfig(event.player)
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        payloads.forget(event.player.uniqueId)
    }

    private fun sendConfig(player: Player) {
        player.sendPluginMessage(this, CONFIG_CHANNEL, payloads.encodeConfig())
    }
}
