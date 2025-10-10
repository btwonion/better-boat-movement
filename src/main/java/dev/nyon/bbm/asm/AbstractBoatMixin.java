package dev.nyon.bbm.asm;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.nyon.bbm.logic.BbmBoat;
import dev.nyon.bbm.config.Config;
import dev.nyon.bbm.config.ConfigKt;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
//? if >=1.21.3
import net.minecraft.world.entity.vehicle.AbstractBoat;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("AddedMixinMembersNamePattern")
@Pseudo
@Mixin(targets = "net.minecraft.world.entity.vehicle.AbstractBoat")
public class AbstractBoatMixin implements BbmBoat {

    @Unique
    private boolean jumpCollision = false;

    @Override
    public void setJumpCollision(boolean b) {
        jumpCollision = b;
    }

    @Override
    public boolean getJumpCollision() {
        return jumpCollision;
    }

    /*? if >=1.21.3 {*/
    @Shadow
    private AbstractBoat.Status status;
    @Unique
    private AbstractBoat instance = (AbstractBoat) (Object) this;

    @SuppressWarnings("resource")
    @Unique
    private List<BlockState> getCarryingBlocks() {
        List<BlockState> states = new ArrayList<>();

        AABB aABB = this.instance.getBoundingBox();
        AABB aABB2 = new AABB(aABB.minX, aABB.minY - 0.001, aABB.minZ, aABB.maxX, aABB.minY, aABB.maxZ);
        int i = Mth.floor(aABB2.minX) - 1;
        int j = Mth.ceil(aABB2.maxX) + 1;
        int k = Mth.floor(aABB2.minY) - 1;
        int l = Mth.ceil(aABB2.maxY) + 1;
        int m = Mth.floor(aABB2.minZ) - 1;
        int n = Mth.ceil(aABB2.maxZ) + 1;
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();

        for (int p = i; p < j; p++) {
            for (int q = m; q < n; q++) {
                int r = (p != i && p != j - 1 ? 0 : 1) + (q != m && q != n - 1 ? 0 : 1);
                if (r != 2) {
                    for (int s = k; s < l; s++) {
                        if (r <= 0 || s != k && s != l - 1) {
                            mutableBlockPos.set(p, s, q);
                            BlockState blockState = instance.level().getBlockState(mutableBlockPos);
                            states.add(blockState);
                        }
                    }
                }
            }
        }

        return states;
    }

    @ModifyExpressionValue(
        method = "floatBoat",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/vehicle/AbstractBoat;getDeltaMovement()Lnet/minecraft/world/phys/Vec3;",
            ordinal = 1
        )
    )
    private Vec3 changeMovement(Vec3 original) {
        if (failsPlayerCondition()) return original;
        BbmBoat bbmBoat = (BbmBoat) instance;

        switch (status) {
            case ON_LAND -> {
                if (!ConfigKt.getActiveConfig()
                    .getBoostOnBlocks() && !ConfigKt.getActiveConfig()
                    .getBoostOnIce()) return original;
                if (ConfigKt.getActiveConfig()
                    .getBoostOnIce()) {
                    List<BlockState> carryingBlocks = getCarryingBlocks();
                    if (carryingBlocks.stream()
                        .noneMatch(state -> state.is(BlockTags.ICE))) return original;
                }
                if (!bbmBoat.getJumpCollision() && !instance.horizontalCollision) return original;
            }
            case IN_WATER -> {
                if (!ConfigKt.getActiveConfig()
                    .getBoostOnWater()) return original;
                if (!bbmBoat.getJumpCollision() && !instance.horizontalCollision) return original;
            }
            case UNDER_WATER, UNDER_FLOWING_WATER -> {
                if (!ConfigKt.getActiveConfig()
                    .getBoostUnderwater()) return original;
            }
            case IN_AIR -> {
                return original;
            }
        }

        bbmBoat.setJumpCollision(false);
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
        return instance.getPassengers().stream()
            .noneMatch(entity -> entity instanceof Player);
    }
    /*?}*/
}
