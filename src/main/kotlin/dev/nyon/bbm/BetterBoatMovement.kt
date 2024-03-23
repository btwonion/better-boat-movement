package dev.nyon.bbm

import dev.nyon.bbm.config.Config
import dev.nyon.konfig.config.config
import dev.nyon.konfig.config.loadConfig
import net.fabricmc.api.EnvType
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.loader.api.FabricLoader
import dev.nyon.bbm.config.config as internalConfig

@Suppress("unused")
object BetterBoatMovement : ModInitializer {
    override fun onInitialize() {
        instantiateConfig()
        when (FabricLoader.getInstance().environmentType) {
            EnvType.CLIENT -> {
                ClientPlayNetworking.registerGlobalReceiver(Config.packetType.id) { _, _, buf, _ ->
                    internalConfig = Config.packetType.read(buf)
                }
            }
            EnvType.SERVER -> {
                ServerPlayConnectionEvents.INIT.register { handler, _ ->
                    ServerPlayNetworking.send(handler.player, internalConfig)
                }
            }
            else -> {}
        }
    }

    private fun instantiateConfig() {
        config(FabricLoader.getInstance().configDir.resolve("better-boat-movement.json"), 1, Config()) { _, _ -> null }
        internalConfig = loadConfig() ?: error("No config settings set!")
    }

    fun saveConfig() {
        dev.nyon.konfig.config.saveConfig(internalConfig)
    }
}
