package net.additionz.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.additionz.AdditionMain;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;

@Mixin(ProjectileEntity.class)
public class ProjectileEntityMixin {

    @Shadow
    @Nullable
    private Entity owner;

    @ModifyArg(method = "Lnet/minecraft/entity/projectile/ProjectileEntity;setVelocity(Lnet/minecraft/entity/Entity;FFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/ProjectileEntity;setVelocity(DDDFF)V", ordinal = 0), index = 4)
    private float setVelocityMixin(float original) {
        if (AdditionMain.CONFIG.inaccuracy_curse_enchantment && owner != null && owner instanceof LivingEntity livingEntity
                && EnchantmentHelper.getLevel(AdditionMain.INACCURACY_CURSE_ENCHANTMENT, livingEntity.getActiveItem()) > 0) {
            return (float) EnchantmentHelper.getLevel(AdditionMain.INACCURACY_CURSE_ENCHANTMENT, livingEntity.getActiveItem()) * 10f;
        }
        return original;
    }
}
