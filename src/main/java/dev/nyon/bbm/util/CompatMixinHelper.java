package dev.nyon.bbm.util;

import dev.nyon.bbm.config.Config;
import dev.nyon.bbm.config.ConfigCacheKt;
import dev.nyon.bbm.config.ConfigKt;
import dev.nyon.bbm.logic.BbmBoat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;
import java.util.Set;

public class CompatMixinHelper {

    public static void checkForHorizontalCollision(Entity entity, List<VoxelShape> blockCollisions, Level world) {
        if (!(entity instanceof BbmBoat bbmBoat)) return;

        Set<Block> allowedSupportingBlocks = ConfigCacheKt.getAllowedCollidingBlocks();
        if (!allowedSupportingBlocks.isEmpty()) {
            boolean correctCollision = blockCollisions.stream().anyMatch(shape -> {
                BlockPos blockPos = new BlockPos(
                    shape.getCoords(Direction.Axis.X).getFirst().intValue(),
                    shape.getCoords(Direction.Axis.Y).getFirst().intValue(),
                    shape.getCoords(Direction.Axis.Z).getFirst().intValue()
                );
                BlockState blockState = world.getBlockState(blockPos);
                return allowedSupportingBlocks.contains(blockState.getBlock());
            });

            if (correctCollision) bbmBoat.setCorrectCollision(true);
        }
    }

    public static AABB expandBox(AABB original, Entity entity) {
        if (!(entity instanceof BbmBoat bbmBoat)) return original;
        if (!bbmBoat.getExpandBb()) return original;
        if (!(entity instanceof /*$ boat {*/net.minecraft.world.entity.vehicle.boat.AbstractBoat/*$}*/ boat)) return original;
        Config config = ConfigKt.getActiveConfig();
        if (config == null) return original;

        if (
            config.getBoosting().getOnlyForPlayers()
                && boat.getPassengers()
                .stream()
                .filter(passenger -> passenger instanceof Player)
                .toList()
                .isEmpty()
        ) return original;

        return original.inflate(
            config.getBoosting().getExtraCollisionDetectionRange(),
            0,
            config.getBoosting().getExtraCollisionDetectionRange()
        );
    }
}
