package dev.nyon.bbm

import dev.nyon.bbm.config.Config
import dev.nyon.konfig.config.config
import dev.nyon.konfig.config.loadConfig
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.loader.api.FabricLoader

lateinit var config: Config
var runningWithServer = false

@Suppress("unused")
object BetterBoatMovement : ModInitializer, ClientModInitializer {
    override fun onInitialize() {
        instantiateConfig()
        ServerPlayConnectionEvents.INIT.register { handler, _ ->
            ServerPlayNetworking.send(handler.player, config)
        }
    }

    override fun onInitializeClient() {
        instantiateConfig()

        ClientPlayNetworking.registerGlobalReceiver(Config.packetType.id) { _, _, buf, _ ->
            runningWithServer = true
            config = Config.packetType.read(buf)
        }

        ClientPlayConnectionEvents.INIT.register { _, _ ->
            runningWithServer = false
        }
    }

    private fun instantiateConfig() {
        config(FabricLoader.getInstance().configDir.resolve("better-boat-movement.json"), 1, Config()) { _, _ -> null}
        config = loadConfig() ?: error("No config settings set!")
    }

    fun saveConfig() {
        dev.nyon.konfig.config.saveConfig(config)
    }
}
