package net.additionz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.additionz.AdditionMain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.GlassBottleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

@Mixin(GlassBottleItem.class)
public class GlassBottleItemMixin {

    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/TypedActionResult;success(Ljava/lang/Object;Z)Lnet/minecraft/util/TypedActionResult;", ordinal = 1))
    private void useMixin(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> info) {
        if (user.isSubmergedInWater() && user.getAir() < user.getMaxAir())
            user.setAir(user.getAir() + AdditionMain.CONFIG.botte_air_amount > user.getMaxAir() ? user.getMaxAir() : user.getAir() + AdditionMain.CONFIG.botte_air_amount);
    }
}
