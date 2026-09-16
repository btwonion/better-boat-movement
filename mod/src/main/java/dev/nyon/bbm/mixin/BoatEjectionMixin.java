package dev.nyon.bbm.mixin;

import dev.nyon.bbm.config.ConfigRepository;
import dev.nyon.bbm.config.GameplayConfigSnapshot;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(AbstractBoat.class)
public class BoatEjectionMixin {
    @Unique
    private final AbstractBoat bbm$boat = (AbstractBoat) (Object) this;

    @ModifyConstant(method = "tick", constant = @Constant(floatValue = 60.0F, ordinal = 0))
    private float bbm$ejectionTicks(float vanillaValue) {
        GameplayConfigSnapshot config = ConfigRepository.INSTANCE.snapshotFor(bbm$boat.level().isClientSide());
        return config == null ? vanillaValue : config.getPlayerEjectTicks();
    }
}
