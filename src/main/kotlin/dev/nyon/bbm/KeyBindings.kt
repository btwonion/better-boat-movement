package dev.nyon.bbm

import dev.nyon.bbm.extensions.keyMapping
import org.lwjgl.glfw.GLFW

object KeyBindings {
    val jumpKeyBind by lazy {
        keyMapping("key.bbm.jump", GLFW.GLFW_KEY_H)
    }

    fun register() {
        //? if fabric {
        net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper.registerKeyMapping(jumpKeyBind)
        //?} else {
        /*dev.nyon.klf.MOD_BUS.addListener<net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent> {
            it.register(jumpKeyBind)
        }
        *///?}
    }
}