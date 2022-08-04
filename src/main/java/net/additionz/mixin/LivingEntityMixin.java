package net.additionz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.additionz.AdditionMain;
import net.additionz.access.AttackTimeAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Items;
import net.minecraft.world.World;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements AttackTimeAccess {

    @Shadow
    private int lastAttackedTime;

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "damage", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/LivingEntity;despawnCounter:I", ordinal = 0), cancellable = true)
    private void damageMixin(DamageSource source, float amount, CallbackInfoReturnable<Boolean> info) {
        if (AdditionMain.CONFIG.chainmail_spike_protection && (source.equals(DamageSource.CACTUS) || source.equals(DamageSource.SWEET_BERRY_BUSH))
                && (Object) this instanceof LivingEntity LivingEntity
                && (LivingEntity.getEquippedStack(EquipmentSlot.HEAD).isOf(Items.CHAINMAIL_HELMET) || LivingEntity.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.CHAINMAIL_CHESTPLATE)
                        || LivingEntity.getEquippedStack(EquipmentSlot.LEGS).isOf(Items.CHAINMAIL_LEGGINGS) || LivingEntity.getEquippedStack(EquipmentSlot.FEET).isOf(Items.CHAINMAIL_BOOTS)))
            info.setReturnValue(false);
    }

    @Inject(method = "getVelocityMultiplier", at = @At("HEAD"), cancellable = true)
    protected void getVelocityMultiplierMixin(CallbackInfoReturnable<Float> info) {
        if (AdditionMain.CONFIG.path_block_speed_boost && this.world.getBlockState(this.getVelocityAffectingPos()).isIn(AdditionMain.PATH_BLOCKS))
            info.setReturnValue(1.22F);
    }

    @Override
    public void setLastAttackedTime(int time) {
        this.lastAttackedTime = time;
    }

}
