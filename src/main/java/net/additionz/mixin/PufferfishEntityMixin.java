package net.additionz.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.additionz.AdditionMain;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.PufferfishEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;

@Mixin(PufferfishEntity.class)
public class PufferfishEntityMixin {

    @Inject(method = "onPlayerCollision", at = @At("HEAD"), cancellable = true)
    private void onPlayerCollisionMixin(PlayerEntity player, CallbackInfo info) {
        if (AdditionMain.CONFIG.chainmail_spike_protection
                && (player.getEquippedStack(EquipmentSlot.HEAD).isOf(Items.CHAINMAIL_HELMET) || player.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.CHAINMAIL_CHESTPLATE)
                        || player.getEquippedStack(EquipmentSlot.LEGS).isOf(Items.CHAINMAIL_LEGGINGS) || player.getEquippedStack(EquipmentSlot.FEET).isOf(Items.CHAINMAIL_BOOTS)))
            info.cancel();
    }

    @Inject(method = "sting", at = @At("HEAD"), cancellable = true)
    private void stingMixin(MobEntity mob, CallbackInfo info) {
        if (AdditionMain.CONFIG.chainmail_spike_protection
                && (mob.getEquippedStack(EquipmentSlot.HEAD).isOf(Items.CHAINMAIL_HELMET) || mob.getEquippedStack(EquipmentSlot.CHEST).isOf(Items.CHAINMAIL_CHESTPLATE)
                        || mob.getEquippedStack(EquipmentSlot.LEGS).isOf(Items.CHAINMAIL_LEGGINGS) || mob.getEquippedStack(EquipmentSlot.FEET).isOf(Items.CHAINMAIL_BOOTS)))
            info.cancel();
    }
}
