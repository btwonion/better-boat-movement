package dev.nyon.bbm.asm;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.nyon.bbm.config.ConfigCacheKt;
import dev.nyon.bbm.extensions.NetworkingKt;
import dev.nyon.bbm.logic.BbmBoat;
import dev.nyon.bbm.config.Config;
import dev.nyon.bbm.config.ConfigKt;
import dev.nyon.bbm.logic.JumpCollisionPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
//? if >=1.21.3
import net.minecraft.world.entity.vehicle.AbstractBoat;
//? if <1.21.3
/*import net.minecraft.world.entity.vehicle.Boat;*/
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;
import java.util.Set;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    protected abstract Vec3 collide(Vec3 movement);

    @Shadow
    private static Vec3 collideWithShapes(
        Vec3 movement,
        AABB entityBoundingBox,
        List<VoxelShape> shapes
    ) {
        return null;
    }

    @Unique
    private boolean expandCollision = false;

    @Unique
    private Entity instance = (Entity) (Object) this;

    @ModifyArg(
        method = "collide",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;collideBoundingBox(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Lnet/minecraft/world/level/Level;Ljava/util/List;)Lnet/minecraft/world/phys/Vec3;",
            ordinal = 0
        ),
        index = 2
    )
    private AABB changeBoatBox(
        Entity entity,
        Vec3 movement,
        AABB box,
        Level world,
        List<VoxelShape> shapes
    ) {
        if (!expandCollision) return box;
        if (!(entity instanceof /*? if >=1.21.3 {*/ AbstractBoat boat /*?} else {*/ /*Boat boat *//*?}*/)) return box;
        Config config = ConfigKt.getActiveConfig();
        if (config == null) return box;

        if (
            config.getBoosting().getOnlyForPlayers()
                && boat.getPassengers()
                .stream()
                .filter(passenger -> passenger instanceof Player)
                .toList()
                .isEmpty()
        ) return box;

        return box.inflate(
            config.getBoosting().getExtraCollisionDetectionRange(),
            0,
            config.getBoosting().getExtraCollisionDetectionRange()
        );
    }

    @Redirect(
        method = "collideBoundingBox",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;collideWithShapes(Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Ljava/util/List;)Lnet/minecraft/world/phys/Vec3;"
        )
    )
    private static Vec3 checkForHorizontalCollision(
        Vec3 vec3,
        AABB bB,
        List<VoxelShape> foundCollisions,
        @Nullable Entity entity,
        Vec3 movement,
        AABB entityBoundingBox,
        Level world,
        List<VoxelShape> expectedCollisions
    ) {
        if (!(entity instanceof BbmBoat bbmBoat)) return collideWithShapes(vec3, bB, foundCollisions);

        Set<Block> allowedSupportingBlocks = ConfigCacheKt.getAllowedCollidingBlocks();
        if (!allowedSupportingBlocks.isEmpty()) {
            boolean correctCollision = foundCollisions.stream().anyMatch(shape -> {
                BlockPos blockPos = new BlockPos(
                    shape.getCoords(Direction.Axis.X).getFirst().intValue(),
                    shape.getCoords(Direction.Axis.Y).getFirst().intValue(),
                    shape.getCoords(Direction.Axis.Z).getFirst().intValue()
                );
                BlockState blockState = world.getBlockState(blockPos);
                return allowedSupportingBlocks.contains(blockState.getBlock());
            });

            bbmBoat.setCorrectCollision(correctCollision);
        }
        return collideWithShapes(vec3, bB, foundCollisions);
    }

    @ModifyExpressionValue(
        method = "move",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;collide(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;"
        )
    )
    private Vec3 setBoatCollision(Vec3 movement) {
        Vec3 original = collide(movement);
        Config config = ConfigKt.getActiveConfig();
        if (config == null) return original;
        if (config.getBoosting().getExtraCollisionDetectionRange() == 0.0) return original;
        if (!(instance instanceof /*? if >=1.21.3 {*/ AbstractBoat boat /*?} else {*/ /*Boat boat *//*?}*/ && instance instanceof BbmBoat bbmBoat)) return original;
        if (!boat.hasControllingPassenger()) return original;

        expandCollision = true;
        Vec3 withFakeBb = collide(movement);
        expandCollision = false;
        boolean xEqual = Mth.equal(movement.x, withFakeBb.x);
        boolean zEqual = Mth.equal(movement.z, withFakeBb.z);
        if (xEqual && zEqual) return original;
        if (!ConfigCacheKt.getAllowedCollidingBlocks().isEmpty() && !bbmBoat.getCorrectCollision()) return original;
        // Set the jump collision for server-executed entities that may control the boat
        bbmBoat.setJumpCollision(true);

        // Set the jump collision for players controlling the boat
        LivingEntity controllingPassenger = instance.getControllingPassenger();
        if (!(controllingPassenger instanceof ServerPlayer player)) return original;
        var id = /*? if >1.21.3 {*/ instance.getUUID() /*?} else {*/ /*instance.getId() *//*?}*/;
        NetworkingKt.sendToClient(player, new JumpCollisionPacket(id));

        return original;
    }
}
