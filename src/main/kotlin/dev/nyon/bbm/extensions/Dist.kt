package dev.nyon.bbm.extensions

import dev.nyon.bbm.BetterBoatMovementEntrypoint

val isClient: Boolean = /*? if fabric {*/ net.fabricmc.loader.api.FabricLoader.getInstance().environmentType == net.fabricmc.api.EnvType.CLIENT /*?} else {*/
    /*BetterBoatMovementEntrypoint.dist == net.neoforged.api.distmarker.Dist.CLIENT *//*?}*/