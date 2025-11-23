package dev.nyon.bbm.asm.compat.lithium;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import dev.nyon.bbm.logic.BbmBoat;
import dev.nyon.bbm.util.CompatMixinHelper;
import net.caffeinemc.mods.lithium.common.util.collections.LazyList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(
    value = Entity.class,
    priority = 1500
)
public class LithiumEntityMixin {

    @TargetHandler(
        mixin = "net.caffeinemc.mods.lithium.mixin.entity.collisions.movement.EntityMixin",
        name = "lithium$CollideMovement"
    )
    @ModifyVariable(
        method = "@MixinSquared:Handler",
        at = @At("HEAD"),
        argsOnly = true,
        index = 2
    )
    private static AABB changeBoatBox(
        AABB original,
        @Local(
            ordinal = 0,
            argsOnly = true
        ) Entity entity
    ) {
        if (entity == null) return original;
        return CompatMixinHelper.expandBox(original, entity);
    }

    @TargetHandler(
        mixin = "net.caffeinemc.mods.lithium.mixin.entity.collisions.movement.EntityMixin",
        name = "lithium$CollideMovement"
    )
    @Inject(
        method = "@MixinSquared:Handler",
        at = @At(
            value = "TAIL"
        )
    )
    private static void checkForHorizontalCollisionLithium(
        @Nullable Entity entity,
        Vec3 movement,
        AABB entityBoundingBox,
        Level world,
        List<VoxelShape> entityCollisions,
        LocalBooleanRef requireAddEntities,
        CallbackInfoReturnable<Vec3> cir,
        @Local(name = "blockCollisions")
        LazyList<VoxelShape> blockCollisions
        ) {
        if (!(entity instanceof BbmBoat)) return;

        CompatMixinHelper.checkForHorizontalCollision(entity, blockCollisions, world);
    }
}
