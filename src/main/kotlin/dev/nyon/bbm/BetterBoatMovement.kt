package dev.nyon.bbm

import dev.nyon.bbm.config.Config
import dev.nyon.bbm.config.migrate
import dev.nyon.konfig.config.config
import dev.nyon.konfig.config.loadConfig
import net.fabricmc.api.EnvType
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
//? if >=1.20.5
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
        //? if >=1.20.5
        PayloadTypeRegistry.playS2C().register(Config.packetType, Config.codec)
        when (FabricLoader.getInstance().environmentType) {
            EnvType.CLIENT -> {
                /*? if <1.20.5 {*//*ClientPlayNetworking.registerGlobalReceiver(Config.packetType.id) { _, _, buf, _ ->
                    serverConfig = Config.packetType.read(buf)
                }
                *//*?} else {*/
                ClientPlayConnectionEvents.INIT.register { _, _ ->
                    ClientPlayNetworking.registerReceiver(Config.packetType) { packet, _ ->
                        serverConfig = packet
                    }
                }/*?}*/

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
        config(
            FabricLoader.getInstance().configDir.resolve("better-boat-movement.json"),
            2,
            Config()
        ) { element, version -> migrate(element, version) }
        internalConfig = loadConfig()
    }

    fun saveConfig() {
        dev.nyon.konfig.config.saveConfig(internalConfig)
    }
}
