package dev.nyon.bbm.asm;

import dev.nyon.bbm.KeyBindings;
import dev.nyon.bbm.config.ConfigKt;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Inject(
        method = "stop",
        at = @At("TAIL")
    )
    private void saveConfig(CallbackInfo ci) {
        ConfigKt.saveConfig();
    }

    @Inject(
        method = "tick",
        at = @At("TAIL")
    )
    public void onTick(CallbackInfo ci) {
        KeyBindings.INSTANCE.handleKeyBindings();
    }
}
