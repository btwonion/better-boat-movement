package dev.nyon.bbm.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.nyon.bbm.movement.BoatMovementController;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractBoat.class)
public class BoatMovementMixin {
    @Shadow
    private AbstractBoat.Status status;

    @Unique
    private final AbstractBoat bbm$boat = (AbstractBoat) (Object) this;

    @ModifyExpressionValue(
        method = "floatBoat",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/vehicle/boat/AbstractBoat;getDeltaMovement()Lnet/minecraft/world/phys/Vec3;",
            ordinal = 1
        )
    )
    private Vec3 bbm$applyAutomaticBoost(Vec3 original) {
        return BoatMovementController.INSTANCE.automaticVelocity(bbm$boat, status, original);
    }
}
