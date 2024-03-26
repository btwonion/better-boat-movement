package dev.nyon.bbm.asm;

import dev.nyon.bbm.config.ConfigKt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Boat.class)
class BoatMixin {
    @Unique
    private Boat instance = (Boat) (Object) this;

    @Unique
    private int wallHitCooldown;

    @Inject(
        method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;)V",
        at = @At("RETURN")
    )
    private void initStepHeight(
        EntityType<? extends Boat> entityType,
        Level level,
        CallbackInfo ci
    ) {
        instance.setMaxUpStep(ConfigKt.config.getStepHeight());
    }

    @Inject(
        method = "tick",
        at = @At("HEAD")
    )
    private void checkWall(CallbackInfo ci) {
        if (failsPlayerCondition()) return;
        if (wallHitCooldown > 0) wallHitCooldown--;
        else if (instance.horizontalCollision) {
            wallHitCooldown = ConfigKt.config.getWallHitCooldownTicks();
            instance.setDeltaMovement(instance.getDeltaMovement()
                .add(0, ConfigKt.config.getStepHeight(), 0));
        }
    }

    @Inject(
        method = "tick",
        at = @At("TAIL")
    )
    private void boostUnderwater(CallbackInfo ci) {
        if (!instance.isUnderWater()) return;
        if (failsPlayerCondition()) return;
        if (!ConfigKt.config.getBoostUnderwater()) return;
        instance.setDeltaMovement(instance.getDeltaMovement()
            .add(0, ConfigKt.config.getStepHeight() / 2, 0));
    }

    @ModifyConstant(
        method = "tick",
        constant = @Constant(
            floatValue = 60.0F,
            ordinal = 0
        )
    )
    private float changeEjectTime(float constant) {
        return ConfigKt.config.getPlayerEjectTicks();
    }

    @Unique
    private boolean failsPlayerCondition() {
        if (!ConfigKt.config.getOnlyForPlayers()) return false;
        return instance.getPassengers()
            .stream()
            .noneMatch(entity -> entity.getType() == EntityType.PLAYER);
    }
}
