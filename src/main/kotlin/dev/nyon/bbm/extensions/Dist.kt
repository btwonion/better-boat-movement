package dev.nyon.bbm.extensions

val isClient: Boolean = /*? if fabric {*/ net.fabricmc.loader.api.FabricLoader.getInstance().environmentType == net.fabricmc.api.EnvType.CLIENT /*?} else {*/
    /*net.neoforged.fml.loading.FMLLoader.getDist() == net.neoforged.api.distmarker.Dist.CLIENT *//*?}*/