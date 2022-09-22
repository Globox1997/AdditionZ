package net.additionz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.additionz.AdditionMain;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {

    @Inject(method = "Lnet/minecraft/entity/ItemEntity;<init>(Lnet/minecraft/world/World;DDDLnet/minecraft/item/ItemStack;)V", at = @At("TAIL"))
    private void initMixin(World world, double x, double y, double z, ItemStack stack, CallbackInfo info) {
        if (AdditionMain.CONFIG.custom_item_name_non_despawn && getStack().hasCustomName())
            setNeverDespawn();
    }

    @Shadow
    public void setNeverDespawn() {
    }

    @Shadow
    public ItemStack getStack() {
        return null;
    }
}
