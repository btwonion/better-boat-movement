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
//? if >=1.20.5
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.api.EnvType

object BetterBoatMovementEntrypoint : ModInitializer {
    override fun onInitialize() {
        instantiateConfig(FabricLoader.getInstance().configDir.resolve("better-boat-movement.json"))
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
                serverConfig = config

                ServerPlayConnectionEvents.INIT.register { handler, _ ->
                    ServerPlayNetworking.send(handler.player, config)
                }
            }
            else -> {}
        }
    }
}

/*?} else if neoforge {*/
/*import dev.nyon.bbm.config.generateYaclScreen
import net.minecraft.server.level.ServerPlayer
import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.ModLoadingContext
import net.neoforged.fml.common.Mod
import net.neoforged.fml.loading.FMLLoader
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent
import net.neoforged.neoforge.network.PacketDistributor
import thedarkcolour.kotlinforforge.neoforge.forge.DIST
import thedarkcolour.kotlinforforge.neoforge.forge.FORGE_BUS

/^? if >=1.20.5 {^/
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler
/^?} else {^/
/^import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent
import net.minecraft.network.FriendlyByteBuf
^//^?}^/
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

typealias CSF = /^? if <1.20.5 {^/ /^net.neoforged.neoforge.client.ConfigScreenHandler.ConfigScreenFactory ^//^?} else {^/ net.neoforged.neoforge.client.gui.IConfigScreenFactory /^?}^/

@Mod("bbm")
object BetterBoatMovementEntrypoint {
    init {
        instantiateConfig(FMLLoader.getGamePath().resolve("config/better-boat-movement.json"))

        /^? if >=1.20.5 {^/
        MOD_BUS.addListener<RegisterPayloadHandlersEvent> { event ->
            val registrar = event.registrar("4")
            registrar.playToClient(Config.packetType, Config.codec, DirectionalPayloadHandler(
                { config, _ ->
                    serverConfig = config
                }, { _, _ -> }
            ))
        }
        /^?} else {^/
        /^MOD_BUS.addListener<RegisterPayloadHandlerEvent> { event ->
            val registrar = event.registrar("4")
            registrar.play(Config.identifier, FriendlyByteBuf.Reader{ buf -> Config(buf) }) { handler ->
                handler.client { config, _ -> serverConfig = config }.server { _, _ -> }
            }
        }
        ^//^?}^/

        when (DIST) {
            Dist.DEDICATED_SERVER -> {
                serverConfig = config
                FORGE_BUS.addListener<PlayerLoggedInEvent> { event ->
                    val player = event.entity
                    if (player !is ServerPlayer) return@addListener
                    /^? if >=1.20.5 {^/ PacketDistributor.sendToPlayer(player, serverConfig!!)
                    /^?} else {^/ /^PacketDistributor.PLAYER.with(player).send(serverConfig) ^//^?}^/
                }
            }

            Dist.CLIENT -> {
                FORGE_BUS.addListener<PlayerLoggedOutEvent> {
                    serverConfig = null
                }
            }
            else -> {}
        }

        ModLoadingContext.get().registerExtensionPoint(CSF::class.java) {
            CSF { _, parent -> generateYaclScreen(parent) }
        }
    }
}
*//*?} else {*/
/*import dev.nyon.bbm.config.generateYaclScreen
import dev.nyon.bbm.extensions.resourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.loading.FMLLoader
import net.minecraftforge.network.NetworkDirection
import net.minecraftforge.network.NetworkRegistry
import net.minecraftforge.network.PacketDistributor
import thedarkcolour.kotlinforforge.forge.DIST
import thedarkcolour.kotlinforforge.forge.FORGE_BUS
import java.util.*

@Mod("bbm")
object BetterBoatMovementEntrypoint {
    init {
        instantiateConfig(FMLLoader.getGamePath().resolve("config/better-boat-movement.json"))

        val channel = NetworkRegistry.newSimpleChannel(resourceLocation("better-boat-movement:channel"), { "4" }, { true }, { true })
        channel.registerMessage(
            0,
            Config::class.java,
            { config, buf -> config.write(buf) },
            { buf -> Config(buf) },
            { config, context ->
                serverConfig = config
                context.get().packetHandled = true
            },
            Optional.of(NetworkDirection.PLAY_TO_CLIENT)
        )

        when (DIST) {
            Dist.DEDICATED_SERVER -> {
                serverConfig = config
                FORGE_BUS.addListener<PlayerEvent.PlayerLoggedInEvent> { event ->
                    val player = event.entity
                    if (player !is ServerPlayer) return@addListener
                    channel.send(PacketDistributor.PLAYER.with { player }, serverConfig)
                }
            }

            Dist.CLIENT -> {
                FORGE_BUS.addListener<PlayerEvent.PlayerLoggedOutEvent> {
                    serverConfig = null
                }
            }

            else -> {}
        }

        // yacl 1.20.1 forge doesn't contain kotlin dsl
        /^? if >1.20.1 {^/
        /^ModLoadingContext.get().registerExtensionPoint(ConfigScreenFactory::class.java) {
            ConfigScreenFactory { _, parent -> generateYaclScreen(parent) }
        }
        ^//^?}^/
    }
}
*//*?}*/

private fun instantiateConfig(path: Path) {
    config(
        path,
        4,
        Config()
    ) { element, version -> migrate(element, version) }
    config = loadConfig()
}