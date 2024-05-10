package dev.nyon.bbm

/*? if <1.20.5 {*/
import dev.nyon.bbm.config.Config
import dev.nyon.konfig.config.config
import dev.nyon.konfig.config.loadConfig
import net.fabricmc.api.EnvType
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.loader.api.FabricLoader
import dev.nyon.bbm.config.config as internalConfig

var serverConfig: Config? = null

@Suppress("unused")
object BetterBoatMovement : ModInitializer {
    override fun onInitialize() {
        instantiateConfig()
        when (FabricLoader.getInstance().environmentType) {
            EnvType.CLIENT -> {
                ClientPlayNetworking.registerGlobalReceiver(Config.packetType.id) { _, _, buf, _ ->
                    serverConfig = Config.packetType.read(buf)
                }

                ClientPlayConnectionEvents.DISCONNECT.register { _, _ ->
                    serverConfig = null
                }
            }
            EnvType.SERVER -> {
                serverConfig = internalConfig
                ServerPlayConnectionEvents.INIT.register { handler, _ ->
                    ServerPlayNetworking.send(handler.player, internalConfig)
                }
            }
            else -> {}
        }
    }

    private fun instantiateConfig() {
        config(FabricLoader.getInstance().configDir.resolve("better-boat-movement.json"), 1, Config()) { _, _ -> null }
        internalConfig = loadConfig()
    }

    fun saveConfig() {
        dev.nyon.konfig.config.saveConfig(internalConfig)
    }
}

/*?} else {*//*
import dev.nyon.bbm.config.Config
import dev.nyon.konfig.config.config
import dev.nyon.konfig.config.loadConfig
import net.fabricmc.api.EnvType
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.loader.api.FabricLoader
import dev.nyon.bbm.config.config as internalConfig

var serverConfig: Config? = null

@Suppress("unused")
object BetterBoatMovement : ModInitializer {
    override fun onInitialize() {
        instantiateConfig()
        when (FabricLoader.getInstance().environmentType) {
            EnvType.CLIENT -> {
                ClientPlayConnectionEvents.INIT.register { _, _ ->
                    PayloadTypeRegistry.playS2C().register(Config.packetType, Config.codec)
                    ClientPlayNetworking.registerGlobalReceiver(Config.packetType) { packet, _ ->
                        serverConfig = packet
                    }
                }

                ClientPlayConnectionEvents.DISCONNECT.register { _, _ ->
                    serverConfig = null
                }
            }
            EnvType.SERVER -> {
                serverConfig = internalConfig
                ServerPlayConnectionEvents.INIT.register { handler, _ ->
                    ServerPlayNetworking.send(handler.player, internalConfig)
                }
            }
            else -> {}
        }
    }

    private fun instantiateConfig() {
        config(FabricLoader.getInstance().configDir.resolve("better-boat-movement.json"), 1, Config()) { _, _ -> null }
        internalConfig = loadConfig()
    }

    fun saveConfig() {
        dev.nyon.konfig.config.saveConfig(internalConfig)
    }
}
*//*?} */
