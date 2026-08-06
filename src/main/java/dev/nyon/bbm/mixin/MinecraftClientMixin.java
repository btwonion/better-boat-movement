package dev.nyon.bbm.mixin;

import dev.nyon.bbm.client.ClientInputHandler;
import dev.nyon.bbm.config.ConfigRepository;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftClientMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void bbm$handleInput(CallbackInfo ci) {
        ClientInputHandler.INSTANCE.tick((Minecraft) (Object) this);
    }

    @Inject(method = "stop", at = @At("TAIL"))
    private void bbm$saveConfig(CallbackInfo ci) {
        ConfigRepository.INSTANCE.save(false);
    }
}
