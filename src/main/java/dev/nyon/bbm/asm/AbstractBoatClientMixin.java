package dev.nyon.bbm.asm;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
/*? if >=1.21.3 {*/
import dev.nyon.bbm.KeyBindings;
import dev.nyon.bbm.config.Config;
import dev.nyon.bbm.config.ConfigKt;
import dev.nyon.bbm.extensions.DistKt;
import /*$ boat {*/net.minecraft.world.entity.vehicle.boat.AbstractBoat/*$}*/;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/*?}*/

@Mixin(/*$ boat {*/net.minecraft.world.entity.vehicle.boat.AbstractBoat/*$}*/.class)
@Pseudo
public class AbstractBoatClientMixin {

    /*? if >=1.21.3 {*/
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
        instance.addDeltaMovement(new Vec3(0.0, config.getStepHeight() * config.getKeybind().getKeybindJumpHeightMultiplier(), 0.0));
    }
    /*?}*/
}
