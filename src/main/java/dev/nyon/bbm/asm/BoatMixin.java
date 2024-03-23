package dev.nyon.bbm.asm;

import dev.nyon.bbm.BetterBoatMovementKt;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Boat.class)
class BoatMixin {
    @Unique
    private final Boat instance = (Boat) (Object) this;

    @Inject(method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;)V", at = @At("TAIL"))
    private void initStepHeight(
        EntityType<? extends Boat> entityType,
        Level level,
        CallbackInfo ci
    ) {
        if (!instance.level().isClientSide || BetterBoatMovementKt.getRunningWithServer())
       instance.setMaxUpStep(BetterBoatMovementKt.config.getGroundStepHeight());
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void updateStepHeight(CallbackInfo ci) {
        if (instance.level().isClientSide && BetterBoatMovementKt.getRunningWithServer())
            instance.setMaxUpStep(BetterBoatMovementKt.config.getGroundStepHeight());
    }

    @Unique
    @SuppressWarnings("SpellCheckingInspection")
    private double approximateFreeboard() {
        return instance.getBbHeight() * (2d / 9d);
    }

    @ModifyConstant(
        method = "isUnderwater",
        constant = @Constant(doubleValue = 0.001, ordinal = 0)
    )
    private double changeUnderwaterHeight(double padding) {
        if (!BetterBoatMovementKt.getRunningWithServer()) return padding;
        return (BetterBoatMovementKt.config.getWaterStepHeight() / 9d) - approximateFreeboard();
    }

    @ModifyVariable(
        method = "checkInWater",
        at = @At(value = "STORE", ordinal = 0), index = 5
    )
    private int changeInWaterCheckHeight(int value) {
        if (!BetterBoatMovementKt.getRunningWithServer()) return value;
        return Mth.ceil(instance.getBoundingBox().maxY);
    }

    @ModifyConstant(
        method = "tick",
        constant = @Constant(floatValue = 60.0F, ordinal = 0)
    )
    private float changeEjectTime(float constant) {
        return BetterBoatMovementKt.config.getPlayerEjectTicks();
    }
}
