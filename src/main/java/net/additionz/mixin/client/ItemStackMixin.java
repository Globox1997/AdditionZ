package net.additionz.mixin.client;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.additionz.AdditionMain;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Inject(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;hasNbt()Z", ordinal = 2), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void getTooltipMixin(@Nullable PlayerEntity player, TooltipContext context, CallbackInfoReturnable<List<Text>> info, List<Text> list) {
        if (AdditionMain.CONFIG.show_nbt_tooltip) {
            ItemStack stack = (ItemStack) (Object) this;
            if (stack != null && stack.hasNbt()) {
                list.add(Text.translatable("text.additionz.nbt"));
                String fullString = stack.getNbt().asString();
                // fullString = fullString.replaceFirst("{", "");
                // fullString = fullString.replaceAll("}$", "");
                String[] strings = fullString.split(",");

                for (int i = 0; i < strings.length; i++) {
                    list.add(Text.of(strings[i]));
                }
            }
        }
    }
}
