package net.additionz.mixin.compat;

import com.li64.tide.registries.entities.misc.fishing.TideFishingHook;
import com.llamalad7.mixinextras.sugar.Local;
import net.additionz.AdditionMain;
import net.levelz.entity.LevelExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TideFishingHook.class)
public class TideFishingHookMixin {

    @Inject(method = "retrieve(Lnet/minecraft/item/ItemStack;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/player/PlayerEntity;)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z", ordinal = 0))
    private void retrieveMixin(ItemStack rod, ServerWorld serverWorld, PlayerEntity playerEntity, CallbackInfoReturnable<Integer> info, @Local(ordinal = 1) ItemStack stack) {
        if (AdditionMain.FISHING_EXPERIENCE_MAP.containsKey(stack.getItem())) {
            if (AdditionMain.FISHING_EXPERIENCE_MAP.get(stack.getItem()) > 0) {
                if (AdditionMain.isLevelzLoaded) {
                    LevelExperienceOrbEntity.spawn((ServerWorld) playerEntity.getWorld(), playerEntity.getPos(), AdditionMain.FISHING_EXPERIENCE_MAP.get(stack.getItem()));
                }
            }
        }
    }
}
