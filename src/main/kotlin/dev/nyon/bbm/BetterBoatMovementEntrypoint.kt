package dev.nyon.bbm

import dev.nyon.bbm.config.Config
import dev.nyon.bbm.config.config
import dev.nyon.bbm.config.migrate
import dev.nyon.bbm.config.serverConfig
import dev.nyon.konfig.config.config
import dev.nyon.konfig.config.loadConfig
import java.nio.file.Path

/*? if fabric {*/
/*import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.loader.api.FabricLoader

object BetterBoatMovementEntrypoint : ModInitializer, ClientModInitializer {
    override fun onInitialize() {
        instantiateConfig(FabricLoader.getInstance().configDir.resolve("better-boat-movement.json"))

        PayloadTypeRegistry.playS2C().register(Config.packetType, Config.codec)
        serverConfig = config

        ServerPlayConnectionEvents.INIT.register { handler, _ ->
            ServerPlayNetworking.send(handler.player, config)
        }
    }

    override fun onInitializeClient() {
        KeyBindings.register()
        setupClientNetworking()
    }

    private fun setupClientNetworking() {
        net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.INIT.register { _, _ ->
            net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.registerGlobalReceiver(Config.packetType) { packet, _ ->
                serverConfig = packet
            }
        }

        net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents.DISCONNECT.register { _, _ ->
            serverConfig = null
        }
    }
}

*//*?} else if neoforge {*/
import dev.nyon.bbm.config.generateYaclScreen
import dev.nyon.klf.MOD_BUS
import net.minecraft.server.level.ServerPlayer
import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.ModLoadingContext
import net.neoforged.fml.common.Mod
import net.neoforged.fml.loading.FMLLoader
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
//? if <1.21.7
/*import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler*/

@Mod("bbm")
object BetterBoatMovementEntrypoint {
    init {
        instantiateConfig(FMLLoader.getGamePath().resolve("config/better-boat-movement.json"))
        setupDistDependant()
    }

    private fun setupDistDependant() {
        MOD_BUS.addListener<RegisterPayloadHandlersEvent> { event ->
            val registrar = event.registrar("bbm").versioned("6")
            //? if <1.21.7 {
            /*registrar.playToClient(Config.packetType, Config.codec, DirectionalPayloadHandler(
                { config, _ ->
                    serverConfig = config
                }, { _, _ -> }
            ))
            *///?} else {
            registrar.playToClient(Config.packetType, Config.codec)
            //?}
        }

        when (FMLLoader.getDist()) {
            Dist.DEDICATED_SERVER -> {
                serverConfig = config
                NeoForge.EVENT_BUS.addListener<PlayerLoggedInEvent> { event ->
                    val player = event.entity
                    if (player !is ServerPlayer) return@addListener
                    PacketDistributor.sendToPlayer(player, serverConfig!!)
                }
            }

            Dist.CLIENT -> {
                ModLoadingContext.get().registerExtensionPoint(net.neoforged.neoforge.client.gui.IConfigScreenFactory::class.java) {
                    net.neoforged.neoforge.client.gui.IConfigScreenFactory { _, parent -> generateYaclScreen(parent) }
                }

                KeyBindings.register()
                //? if >=1.21.7 {
                MOD_BUS.addListener<net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent> { event ->
                    event.register(
                        Config.packetType
                    ) { config, _ ->
                        serverConfig = config
                    }
                }
                //?}

                NeoForge.EVENT_BUS.addListener<PlayerLoggedOutEvent> {
                    serverConfig = null
                }
            }
            else -> {}
        }
    }
}
/*?}*/

private fun instantiateConfig(path: Path) {
    config(
        path,
        6,
        Config()
    ) { _, element, version -> migrate(element, version) }
    config = loadConfig()
}