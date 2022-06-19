package net.additionz.mixin;

import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.additionz.AdditionMain;
import net.additionz.network.AdditionServerPacket;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Inject(method = "Lnet/minecraft/item/ItemStack;damage(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;decrement(I)V"), cancellable = true)
    private <T extends LivingEntity> void damage(int amount, T entity, Consumer<T> breakCallback, CallbackInfo info) {
        if (AdditionMain.CONFIG.totem_of_non_breaking && entity instanceof PlayerEntity) {
            PlayerInventory playerInventory = ((PlayerEntity) entity).getInventory();
            for (int i = 0; i < playerInventory.size(); i++) {
                if (playerInventory.getStack(i).getItem().equals(AdditionMain.TOTEM_OF_NON_BREAKING)) {
                    playerInventory.getStack(i).decrement(1);
                    ((ItemStack) (Object) this).setDamage((int) (((ItemStack) (Object) this).getMaxDamage() * 0.95F));
                    AdditionServerPacket.writeS2CTotemOfNonBreakingPacket((ServerPlayerEntity) entity);
                    info.cancel();
                    break;
                }
            }

        }

    }
}
