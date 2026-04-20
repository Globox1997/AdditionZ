package net.additionz.mixin;

import net.additionz.AdditionMain;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(RangedWeaponItem.class)
public class RangedWeaponItemMixin {

    @Inject(method = "createArrowEntity", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void createArrowEntityMixin(World world, LivingEntity shooter, ItemStack weaponStack, ItemStack projectileStack, boolean critical, CallbackInfoReturnable<ProjectileEntity> info, ArrowItem arrowItem2, PersistentProjectileEntity persistentProjectileEntity) {
        if (shooter instanceof PlayerEntity playerEntity) {
            persistentProjectileEntity.setDamage(playerEntity.getAttributeValue(AdditionMain.GENERIC_RANGE_ATTACK_DAMAGE));

        }
    }
}
