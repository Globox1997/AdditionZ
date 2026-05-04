package net.additionz.mixin.compat;

import com.llamalad7.mixinextras.sugar.Local;
import net.additionz.AdditionMain;
import net.levelz.entity.LevelExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = FishingBobberEntity.class, priority = 900)
public class FishingBobberEntityCompatMixin {

    @Inject(method = "use", at = @At(value = "INVOKE_ASSIGN", shift = At.Shift.AFTER, target = "Lnet/minecraft/loot/LootTable;generateLoot(Lnet/minecraft/loot/context/LootContextParameterSet;)Lit/unimi/dsi/fastutil/objects/ObjectArrayList;"))
    private void useMixin(ItemStack usedItem, CallbackInfoReturnable<Integer> info, @Local PlayerEntity playerEntity, @Local List<ItemStack> list) {
        for (ItemStack itemStack : list) {
            if (AdditionMain.FISHING_EXPERIENCE_MAP.containsKey(itemStack.getItem())) {
                if (AdditionMain.FISHING_EXPERIENCE_MAP.get(itemStack.getItem()) > 0) {
                    if (AdditionMain.isLevelzLoaded) {
                        LevelExperienceOrbEntity.spawn((ServerWorld) playerEntity.getWorld(), playerEntity.getPos(), AdditionMain.FISHING_EXPERIENCE_MAP.get(itemStack.getItem()));
                    }
                }
            }
        }
    }
}
