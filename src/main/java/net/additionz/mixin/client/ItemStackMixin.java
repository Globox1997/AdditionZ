package net.additionz.mixin.client;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.additionz.AdditionMain;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

@Environment(EnvType.CLIENT)
@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Inject(method = "getTooltip", at = @At(value = "TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void getTooltipMixin(Item.TooltipContext context, @Nullable PlayerEntity player, TooltipType type, CallbackInfoReturnable<List<Text>> info, List<Text> list) {
        if (AdditionMain.CONFIG.show_component_tooltip) {
            ItemStack stack = (ItemStack) (Object) this;
            if (stack != null && !stack.getComponentChanges().isEmpty()) {
                list.add(Text.translatable("text.additionz.component"));

                for (Entry<ComponentType<?>, Optional<?>> entry : stack.getComponentChanges().entrySet()) {
                    if (entry.getValue().isEmpty()) {
                        continue;
                    }

                    list.add(Text.of(Util.registryValueToString(Registries.DATA_COMPONENT_TYPE, entry.getKey())));
                }
            }
        }
    }
}
