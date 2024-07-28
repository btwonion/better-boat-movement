package dev.nyon.bbm.asm;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.nyon.bbm.config.Config;
import dev.nyon.bbm.config.ConfigKt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Boat.class)
abstract class BoatMixin extends Entity {
    @Shadow
    private Boat.Status status;

    public BoatMixin(
        EntityType<?> entityType,
        Level level
    ) {
        super(entityType, level);
    }

    @SuppressWarnings("DataFlowIssue")
    @ModifyExpressionValue(
        method = "floatBoat",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/vehicle/Boat;getDeltaMovement()Lnet/minecraft/world/phys/Vec3;",
            ordinal = 1
        )
    )
    private Vec3 changeMovement(Vec3 original) {
        if (failsPlayerCondition()) return original;
        if (!horizontalCollision) return original;
        if (status == Boat.Status.UNDER_WATER && !ConfigKt.getActiveConfig()
            .getBoostUnderwater()) return original;

        return new Vec3(
            original.x,
            ConfigKt.getActiveConfig()
                .getStepHeight(),
            original.z
        );
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
        return getPassengers().stream()
            .anyMatch(entity -> entity instanceof Player);
    }
}
