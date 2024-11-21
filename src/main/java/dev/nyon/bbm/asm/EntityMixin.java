package dev.nyon.bbm.asm;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.nyon.bbm.BbmBoat;
import dev.nyon.bbm.config.Config;
import dev.nyon.bbm.config.ConfigKt;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
//? if >=1.21.3
/*import net.minecraft.world.entity.vehicle.AbstractBoat;*/
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    protected abstract Vec3 collide(Vec3 movement);

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
        if (!(entity instanceof /*? if >=1.21.3 {*/ /*AbstractBoat boat *//*?} else {*/ Boat boat /*?}*/)) return box;
        Config config = ConfigKt.getActiveConfig();
        if (config == null) return box;

        if (boat.getPassengers()
            .stream()
            .filter(passenger -> passenger instanceof Player)
            .toList()
            .isEmpty() && config.getOnlyForPlayers()) return box;

        return box.inflate(config.getExtraCollisionDetectionRange(), 0, config.getExtraCollisionDetectionRange());
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
        if (config.getExtraCollisionDetectionRange() == 0.0) return original;
        if (!(instance instanceof /*? if >=1.21.3 {*/ /*AbstractBoat boat *//*?} else {*/ Boat boat /*?}*/ && instance instanceof BbmBoat bbmBoat)) return original;
        if (!boat.hasControllingPassenger()) return original;

        expandCollision = true;
        Vec3 withFakeBb = collide(movement);
        expandCollision = false;
        boolean bl = !Mth.equal(movement.x, withFakeBb.x);
        boolean bl2 = !Mth.equal(movement.z, withFakeBb.z);
        bbmBoat.setJumpCollision(bl || bl2);

        return original;
    }
}
