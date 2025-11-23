package dev.nyon.bbm.asm.compat.lithium;

import dev.nyon.bbm.util.CompatMixinHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.List;

@Mixin(Entity.class)
public abstract class BaseEntityMixin {

    @ModifyArgs(
        method = "collideBoundingBox",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;collideWithShapes(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/List;)Lnet/minecraft/world/phys/Vec3;"
        )
    )
    private static void checkForHorizontalCollision(
        Args args,
        @Nullable Entity entity,
        Vec3 movement,
        AABB entityBoundingBox,
        Level world,
        List<VoxelShape> shapes
    ) {
        CompatMixinHelper.checkForHorizontalCollision(entity, args.get(2), world);
    }
}
