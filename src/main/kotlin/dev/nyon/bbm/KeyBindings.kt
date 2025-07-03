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
}