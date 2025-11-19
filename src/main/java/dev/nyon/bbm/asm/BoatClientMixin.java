package dev.nyon.bbm.asm;

import dev.nyon.bbm.KeyBindings;
import dev.nyon.bbm.config.Config;
import dev.nyon.bbm.config.ConfigKt;
import dev.nyon.bbm.extensions.DistKt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Boat.class)
public abstract class BoatClientMixin extends Entity {

    public BoatClientMixin(
        EntityType<?> type,
        Level world
    ) {
        super(type, world);
    }

    /*? if <1.21.3 {*/
    /*@Inject(
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
                && !onGround()
                && !isInWater()
                && !isUnderWater()
        ) return;
        addDeltaMovement(new Vec3(0.0, config.getStepHeight() * config.getKeybind().getKeybindJumpHeightMultiplier(), 0.0));
    }
    *//*?}*/
}
