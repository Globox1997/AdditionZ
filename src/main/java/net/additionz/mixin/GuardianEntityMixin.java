package net.additionz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.additionz.AdditionMain;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.Items;
import net.minecraft.world.World;

@Mixin(GuardianEntity.class)
public abstract class GuardianEntityMixin extends HostileEntity {

    public GuardianEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"), cancellable = true)
    private void damageMixin(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
        if (AdditionMain.CONFIG.chainmail_spike_protection && (((LivingEntity) source.getSource()).getEquippedStack(EquipmentSlot.HEAD).isOf(Items.CHAINMAIL_HELMET)
                || ((LivingEntity) source.getSource()).getEquippedStack(EquipmentSlot.CHEST).isOf(Items.CHAINMAIL_CHESTPLATE)
                || ((LivingEntity) source.getSource()).getEquippedStack(EquipmentSlot.LEGS).isOf(Items.CHAINMAIL_LEGGINGS)
                || ((LivingEntity) source.getSource()).getEquippedStack(EquipmentSlot.FEET).isOf(Items.CHAINMAIL_BOOTS))) {
            super.damage(source, amount);
            info.setReturnValue(false);
        }
    }
}
