package dev.nyon.bbm

import com.mojang.blaze3d.platform.InputConstants
//? if fabric
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
//? if neoforge
/*import net.neoforged.neoforge.network.PacketDistributor*/
import net.minecraft.client.KeyMapping
import org.lwjgl.glfw.GLFW

object KeyBindings {
    val jumpKeyBind by lazy {
        KeyMapping(
            "key.bbm.jump", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_H, "key.bbm.category"
        )
    }

    fun handleKeyBindings() {
        if (jumpKeyBind.isDown) {
            /*? if fabric {*/
            ClientPlayNetworking.send(PressJumpKeybindingPacket)
            /*?} else {*/
            /*PacketDistributor.sendToServer(PressJumpKeybindingPacket)
            *//*?}*/
        }
    }
}