package dev.nyon.bbm.asm;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import dev.nyon.bbm.KeyBindings;
import dev.nyon.bbm.config.Config;
import dev.nyon.bbm.config.ConfigKt;
import dev.nyon.bbm.extensions.DistKt;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractBoat.class)
public class AbstractBoatClientMixin {

    @Unique
    private AbstractBoat instance = (AbstractBoat) (Object) this;

    @Inject(
        method = "tick",
        at = @At("HEAD")
    )
    private void triggerJump(CallbackInfo ci) {
        if (!DistKt.isClient()) return;
        Config config = ConfigKt.getActiveConfig();
        if (config == null) return;
        if (!KeyBindings.INSTANCE.getJumpKeyBind().isDown() || !config.getKeybind().getAllowJumpKeybind()) return;
        if (
            config.getKeybind().getOnlyKeybindJumpOnGroundOrWater()
                && !instance.onGround()
                && !instance.isInWater()
                && !instance.isUnderWater()
        ) return;
        if (instance.getPassengers().stream()
            .noneMatch(entity -> entity instanceof Player)) return;
        instance.addDeltaMovement(new Vec3(0.0, config.getStepHeight() * config.getKeybind().getKeybindJumpHeightMultiplier(), 0.0));
    }
}
