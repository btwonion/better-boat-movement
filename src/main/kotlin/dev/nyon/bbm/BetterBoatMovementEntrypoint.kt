package dev.nyon.bbm

import dev.nyon.bbm.config.Config
import dev.nyon.bbm.config.config
import dev.nyon.bbm.config.migrate
import dev.nyon.bbm.config.serverConfig
import dev.nyon.konfig.config.config
import dev.nyon.konfig.config.loadConfig
import java.nio.file.Path

/*? if fabric {*/
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper

object BetterBoatMovementEntrypoint : ModInitializer {
    override fun onInitialize() {
        instantiateConfig(FabricLoader.getInstance().configDir.resolve("better-boat-movement.json"))
        setupNetworking()
        KeyBindingHelper.registerKeyBinding(KeyBindings.jumpKeyBind)
    }

    private fun setupNetworking() {
        PayloadTypeRegistry.playS2C().register(Config.packetType, Config.codec)
        PayloadTypeRegistry.playC2S().register(PressJumpKeybindingPacket.packetType, PressJumpKeybindingPacket.codec)

        ClientPlayConnectionEvents.INIT.register { _, _ ->
            ClientPlayNetworking.registerGlobalReceiver(Config.packetType) { packet, _ ->
                serverConfig = packet
            }
        }

        ClientPlayConnectionEvents.DISCONNECT.register { _, _ ->
            serverConfig = null
        }

        serverConfig = config
        ServerPlayNetworking.registerGlobalReceiver(PressJumpKeybindingPacket.packetType) { _, context ->
            PressJumpKeybindingPacket.handlePacket(context.player())
        }

        ServerPlayConnectionEvents.INIT.register { handler, _ ->
            ServerPlayNetworking.send(handler.player, config)
        }
    }
}

/*?} else if neoforge {*/
/*import dev.nyon.klf.MOD_BUS
import net.minecraft.server.level.ServerPlayer
import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.common.Mod
import net.neoforged.fml.loading.FMLLoader
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler

@Mod("bbm")
object BetterBoatMovementEntrypoint {
    init {
        instantiateConfig(FMLLoader.getGamePath().resolve("config/better-boat-movement.json"))
        setupNetworking()
        MOD_BUS.addListener<RegisterKeyMappingsEvent> {
            it.register(KeyBindings.jumpKeyBind)
        }
    }

    private fun setupNetworking() {
        MOD_BUS.addListener<RegisterPayloadHandlersEvent> { event ->
            val registrar = event.registrar("bbm").versioned("5")
            registrar.playToClient(Config.packetType, Config.codec, DirectionalPayloadHandler(
                { config, _ ->
                    serverConfig = config
                }, { _, _ -> }
            ))
            registrar.playToServer(PressJumpKeybindingPacket.packetType, PressJumpKeybindingPacket.codec,
                DirectionalPayloadHandler(
                    { _, _ -> },
                    { _, context ->
                        PressJumpKeybindingPacket.handlePacket(context.player())
                    }
                )
            )
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
                NeoForge.EVENT_BUS.addListener<PlayerLoggedOutEvent> {
                    serverConfig = null
                }
            }
            else -> {}
        }
    }
}
*//*?}*/

private fun instantiateConfig(path: Path) {
    config(
        path,
        5,
        Config()
    ) { _, element, version -> migrate(element, version) }
    config = loadConfig()
}