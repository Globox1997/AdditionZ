package net.additionz.mixin;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import net.additionz.AdditionMain;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.registry.entry.RegistryEntry;

@Mixin(ProjectileEntity.class)
public class ProjectileEntityMixin {

    @Shadow
    @Nullable
    private Entity owner;

    @ModifyArg(method = "Lnet/minecraft/entity/projectile/ProjectileEntity;setVelocity(Lnet/minecraft/entity/Entity;FFFFF)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/ProjectileEntity;setVelocity(DDDFF)V", ordinal = 0), index = 4)
    private float setVelocityMixin(float original) {
        if (AdditionMain.CONFIG.inaccuracy_curse_enchantment && owner != null && owner instanceof LivingEntity livingEntity) {
            Optional<RegistryEntry<Enchantment>> optional = livingEntity.getActiveItem().getEnchantments().getEnchantments().stream()
                    .filter(entry -> entry.matchesId(AdditionMain.INACCURACY_CURSE)).findFirst();
            if (optional.isPresent() && !optional.isEmpty()) {
                return (float) EnchantmentHelper.getLevel(optional.get(), livingEntity.getActiveItem()) * 10f;
            }
        }
        return original;
    }
}
