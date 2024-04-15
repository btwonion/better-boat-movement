package dev.nyon.bbm.asm;

import dev.nyon.bbm.config.Config;
import dev.nyon.bbm.config.ConfigKt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Boat.class)
abstract class BoatMixin extends Entity {
    @Unique
    private final Boat instance = (Boat) (Object) this;

    @Unique
    private int wallHitCooldown;

    public BoatMixin(
        EntityType<?> entityType,
        Level level
    ) {
        super(entityType, level);
    }

    @Override
    public float maxUpStep() {
        Config config = ConfigKt.getActiveConfig();
        if (config == null) return 0f;
        return config.getStepHeight();
    }

    @Inject(
        method = "tick",
        at = @At("HEAD")
    )
    private void checkWall(CallbackInfo ci) {
        Config config = ConfigKt.getActiveConfig();
        if (config == null) return;

        if (failsPlayerCondition()) return;
        if (wallHitCooldown > 0) wallHitCooldown--;
        else if (instance.horizontalCollision) {
            wallHitCooldown = config.getWallHitCooldownTicks();
            instance.addDeltaMovement(new Vec3(0, config.getStepHeight(), 0));
        }
    }

    @Inject(
        method = "tick",
        at = @At("TAIL")
    )
    private void boostUnderwater(CallbackInfo ci) {
        if (!instance.isUnderWater()) return;
        Config config = ConfigKt.getActiveConfig();
        if (config == null) return;

        if (failsPlayerCondition()) return;
        if (!config.getBoostUnderwater()) return;
        instance.addDeltaMovement(new Vec3(0, config.getStepHeight() / 2, 0));
    }

    @ModifyConstant(
        method = "tick",
        constant = @Constant(
            floatValue = 60.0F,
            ordinal = 0
        )
    )
    private float changeEjectTime(float constant) {
        Config config = ConfigKt.getActiveConfig();
        if (config == null) return constant;
        return config.getPlayerEjectTicks();
    }

    @Unique
    private boolean failsPlayerCondition() {
        Config config = ConfigKt.getActiveConfig();
        if (config == null) return true;

        if (!config.getOnlyForPlayers()) return false;
        return instance.getPassengers()
            .stream()
            .noneMatch(entity -> entity.getType() == EntityType.PLAYER);
    }
}
