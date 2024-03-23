package dev.nyon.bbm.asm;

import dev.nyon.bbm.BetterBoatMovement;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Inject(method = "stop", at = @At("TAIL"))
    private void saveConfig(CallbackInfo ci) {
        BetterBoatMovement.INSTANCE.saveConfig();
    }
}
