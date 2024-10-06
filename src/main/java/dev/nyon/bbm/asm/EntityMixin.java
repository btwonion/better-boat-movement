package dev.nyon.bbm.asm;

import dev.nyon.bbm.config.Config;
import dev.nyon.bbm.config.ConfigKt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;

@Mixin(Entity.class)
public class EntityMixin {
    @ModifyArg(
        method = "collide",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/Entity;collideBoundingBox(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/Vec3;Lnet/minecraft/world/phys/AABB;Lnet/minecraft/world/level/Level;Ljava/util/List;)Lnet/minecraft/world/phys/Vec3;"
        ),
        index = 2
    )
    private AABB changeCollisionRange(
        Entity entity,
        Vec3 movement,
        AABB box,
        Level world,
        List<VoxelShape> shapes
    ) {
        if (!(entity instanceof Boat boat)) return box;
        Config config = ConfigKt.getActiveConfig();
        if (config == null) return box;

        if (boat.getPassengers()
            .stream()
            .filter(passenger -> passenger instanceof Player)
            .toList()
            .isEmpty() && config.getOnlyForPlayers()) return box;

        return box.inflate(config.getExtraCollisionDetectionRange(), 0, config.getExtraCollisionDetectionRange());
    }
}
