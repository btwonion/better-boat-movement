/*? if fabric {*/
package dev.nyon.bbm.platform.fabric

import dev.nyon.bbm.KeyBindings
import dev.nyon.bbm.bootstrap
import dev.nyon.bbm.config.ConfigRepository
import dev.nyon.bbm.config.reloadCache
import dev.nyon.bbm.network.ConfigSnapshotPayload
import dev.nyon.bbm.network.ConfigSync
import dev.nyon.bbm.network.ManualJumpRequestHandler
import dev.nyon.bbm.network.ManualJumpRequestPayload
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.loader.api.FabricLoader

object FabricEntrypoint : ModInitializer, ClientModInitializer {
    override fun onInitialize() {
        bootstrap(FabricLoader.getInstance().configDir.resolve("better-boat-movement.json"))

        PayloadTypeRegistry.clientboundPlay().register(ConfigSnapshotPayload.TYPE, ConfigSnapshotPayload.CODEC)
        PayloadTypeRegistry.serverboundPlay().register(ManualJumpRequestPayload.TYPE, ManualJumpRequestPayload.CODEC)
        ServerPlayNetworking.registerGlobalReceiver(ManualJumpRequestPayload.TYPE) { payload, context ->
            ManualJumpRequestHandler.handle(context.player(), payload)
        }
        CommonLifecycleEvents.TAGS_LOADED.register { _, _ ->
            reloadCache(ConfigRepository.snapshotFor(clientSide = false))
        }
        ServerPlayConnectionEvents.INIT.register { handler, _ ->
            ServerPlayNetworking.send(handler.player, ConfigSync.serverPayload())
        }
    }

    override fun onInitializeClient() {
        KeyBindings.register()
        ClientPlayNetworking.registerGlobalReceiver(ConfigSnapshotPayload.TYPE) { payload, _ ->
            ConfigSync.receive(payload)
        }
        ClientPlayConnectionEvents.DISCONNECT.register { _, _ -> ConfigSync.disconnect() }
    }
}
/*?}*/
