package dev.nyon.bbm

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.KeyMapping
import org.lwjgl.glfw.GLFW

object KeyBindings {
    val jumpKeyBind by lazy {
        KeyMapping(
            "key.bbm.jump", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_H, "key.bbm.category"
        )
    }

    fun register() {
        //? if fabric {
        /*net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper.registerKeyBinding(jumpKeyBind)
        *///?} else {
        dev.nyon.klf.MOD_BUS.addListener<net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent> {
            it.register(jumpKeyBind)
        }
        //?}
    }
}