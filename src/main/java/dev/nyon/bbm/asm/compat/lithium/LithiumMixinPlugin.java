package dev.nyon.bbm.asm.compat.lithium;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class LithiumMixinPlugin implements IMixinConfigPlugin {
    private boolean isModLoaded(String modName) {
        //? if fabric
        return net.fabricmc.loader.api.FabricLoader.getInstance()
            .isModLoaded(modName);
        //? if neoforge && >1.21.7
        /*return net.neoforged.fml.loading.FMLLoader.getCurrent().getLoadingModList().getModFileById(modName) != null;*/
        //? if neoforge && <=1.21.7
        /*return net.neoforged.fml.loading.FMLLoader.getLoadingModList().getModFileById(modName) != null;*/
    }

    @Override
    public void onLoad(String s) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(
        String targetClassName,
        String mixinClassName
    ) {
        boolean lithiumPresent = isModLoaded("lithium");

        int actualNameBeginning = mixinClassName.lastIndexOf(".");
        String actualName = mixinClassName.substring(actualNameBeginning + 1);

        if (actualName.equals("BaseEntityMixin") && lithiumPresent) return false;
        if (actualName.equals("LithiumEntityMixin") && !lithiumPresent) return false;

        return true;
    }

    @Override
    public void acceptTargets(
        Set<String> set,
        Set<String> set1
    ) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(
        String s,
        ClassNode classNode,
        String s1,
        IMixinInfo iMixinInfo
    ) {
    }

    @Override
    public void postApply(
        String s,
        ClassNode classNode,
        String s1,
        IMixinInfo iMixinInfo
    ) {
    }
}
