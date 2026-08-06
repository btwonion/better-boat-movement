/*? if neoforge {*//*
package dev.nyon.bbm.platform.neoforge

import dev.nyon.bbm.KeyBindings
import dev.nyon.bbm.bootstrap
import dev.nyon.bbm.config.ConfigRepository
import dev.nyon.bbm.config.reloadCache
import dev.nyon.bbm.config.screen.generateYaclScreen
import dev.nyon.bbm.extensions.sendToClient
import dev.nyon.bbm.network.ConfigSnapshotPayload
import dev.nyon.bbm.network.ConfigSync
import dev.nyon.bbm.network.ManualJumpRequestHandler
import dev.nyon.bbm.network.ManualJumpRequestPayload
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
object NeoForgeEntrypoint {
    private val dist: Dist = FMLLoader.getCurrent().dist

    init {
        bootstrap(FMLLoader.getCurrent().gameDir.resolve("config/better-boat-movement.json"))
        registerCommon()
        if (dist == Dist.CLIENT) registerClient()
    }

    private fun registerCommon() {
        MOD_BUS.addListener<RegisterPayloadHandlersEvent> { event ->
            val registrar = event.registrar("1")
            registrar.playToClient(ConfigSnapshotPayload.TYPE, ConfigSnapshotPayload.CODEC)
            registrar.playToServer(ManualJumpRequestPayload.TYPE, ManualJumpRequestPayload.CODEC) { payload, context ->
                ManualJumpRequestHandler.handle(context.player() as ServerPlayer, payload)
            }
        }
        NeoForge.EVENT_BUS.addListener<TagsUpdatedEvent> {
            reloadCache(ConfigRepository.snapshotFor(clientSide = false))
        }
        NeoForge.EVENT_BUS.addListener<PlayerLoggedInEvent> { event ->
            val player = event.entity as? ServerPlayer ?: return@addListener
            sendToClient(player, ConfigSync.serverPayload())
        }
    }

    private fun registerClient() {
        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory::class.java) {
            IConfigScreenFactory { _, parent -> generateYaclScreen(parent) }
        }
        KeyBindings.register()
        MOD_BUS.addListener<RegisterClientPayloadHandlersEvent> { event ->
            event.register(ConfigSnapshotPayload.TYPE) { payload, _ -> ConfigSync.receive(payload) }
        }
        NeoForge.EVENT_BUS.addListener<PlayerLoggedOutEvent> { ConfigSync.disconnect() }
    }
}
*//*?}*/
