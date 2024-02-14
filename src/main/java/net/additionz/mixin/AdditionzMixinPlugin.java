package net.additionz.mixin;

import java.util.List;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import net.fabricmc.loader.api.FabricLoader;

public class AdditionzMixinPlugin implements IMixinConfigPlugin {

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.contains("ProjectileEntityMixin") && !mixinClassName.contains("PersistentProjectileEntity") && FabricLoader.getInstance().isModLoaded("levelz")){
            return false;}
        if ((mixinClassName.contains("SurvivalTrinketSlotMixin") || mixinClassName.contains("TrinketScreenManagerMixin") || mixinClassName.contains("ScreenHandlerMixin")
                || mixinClassName.contains("InventoryScreenMixin") || mixinClassName.contains("CreativeInventoryScreenMixin")) && !FabricLoader.getInstance().isModLoaded("trinkets")) {
            return false;
        }

        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

}