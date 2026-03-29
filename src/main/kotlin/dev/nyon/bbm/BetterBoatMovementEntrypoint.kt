package dev.nyon.bbm

import dev.nyon.bbm.config.Config
import dev.nyon.bbm.config.config
import dev.nyon.bbm.config.migrateConfig
import dev.nyon.bbm.config.reloadCache
import dev.nyon.bbm.config.serverConfig
import dev.nyon.bbm.extensions.sendToClient
import dev.nyon.bbm.logic.JumpCollisionPacket
import dev.nyon.konfig.config.config
import dev.nyon.konfig.config.loadConfig
import java.nio.file.Path

/*? if fabric {*/
import dev.nyon.bbm.extensions.isClient
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.fabricmc.loader.api.FabricLoader

object BetterBoatMovementEntrypoint : ModInitializer, ClientModInitializer {
    override fun onInitialize() {
        instantiateConfig(FabricLoader.getInstance().configDir.resolve("better-boat-movement.json"))
        if (!isClient) serverConfig = config

        PayloadTypeRegistry.clientboundPlay().register(Config.packetType, Config.codec)
        PayloadTypeRegistry.clientboundPlay().register(JumpCollisionPacket.packetType, JumpCollisionPacket.codec)

        CommonLifecycleEvents.TAGS_LOADED.register { _, _ ->
            reloadCache()
        }

        ServerPlayConnectionEvents.INIT.register { handler, _ ->
            sendToClient(handler.player, config)
        }
    }

    override fun onInitializeClient() {
        KeyBindings.register()
        setupClientNetworking()
    }

    private fun setupClientNetworking() {
        ClientPlayConnectionEvents.INIT.register { _, _ ->
            ClientPlayNetworking.registerGlobalReceiver(Config.packetType) { packet, _ ->
                serverConfig = packet
            }

            ClientPlayNetworking.registerGlobalReceiver(JumpCollisionPacket.packetType) { packet, context ->
                packet.handle(context.player())
            }
        }

        ClientPlayConnectionEvents.DISCONNECT.register { _, _ ->
            serverConfig = null
        }
    }
}

/*?} else if neoforge {*//*import dev.nyon.bbm.config.screen.generateYaclScreen
import dev.nyon.klf.MOD_BUS
import net.minecraft.server.level.ServerPlayer
import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.ModLoadingContext
import net.neoforged.fml.common.Mod
import net.neoforged.fml.loading.FMLLoader
import net.neoforged.neoforge.client.gui.IConfigScreenFactory
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.TagsUpdatedEvent
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent

@Mod("bbm")
object BetterBoatMovementEntrypoint {
    private val gamePath = FMLLoader.getCurrent().gameDir
    val dist: Dist = FMLLoader.getCurrent().dist

    init {
        instantiateConfig(gamePath.resolve("config/better-boat-movement.json"))
        setupDistDependant()
    }

    private fun setupDistDependant() {
        MOD_BUS.addListener<RegisterPayloadHandlersEvent> { event ->
            val registrar = event.registrar("bbm").versioned("7")
            registrar.playToClient(Config.packetType, Config.codec)
            registrar.playToClient(JumpCollisionPacket.packetType, JumpCollisionPacket.codec)
        }

        NeoForge.EVENT_BUS.addListener<TagsUpdatedEvent> {
            reloadCache()
        }

        when (dist) {
            Dist.DEDICATED_SERVER -> {
                serverConfig = config
                NeoForge.EVENT_BUS.addListener<PlayerLoggedInEvent> { event ->
                    val player = event.entity
                    if (player !is ServerPlayer) return@addListener
                    sendToClient(player, serverConfig!!)
                }
            }

            Dist.CLIENT -> {
                ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory::class.java) {
                    IConfigScreenFactory { _, parent -> generateYaclScreen(parent) }
                }

                KeyBindings.register()
                MOD_BUS.addListener<RegisterClientPayloadHandlersEvent> { event ->
                    event.register(Config.packetType) { config, _ ->
                        serverConfig = config
                    }
                    event.register(JumpCollisionPacket.packetType) { packet, context ->
                        packet.handle(context.player())
                    }
                }

                NeoForge.EVENT_BUS.addListener<PlayerLoggedOutEvent> {
                    serverConfig = null
                }
            }
        }
    }
}
*//*?}*/

private fun instantiateConfig(path: Path) {
    config(
        path, 7, Config()
    ) { _, element, version -> migrateConfig(element, version) }
    config = loadConfig()
}