package net.additionz.mixin;

import java.util.Iterator;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.additionz.AdditionMain;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    @Inject(method = "getPossibleEntries", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void getPossibleEntriesMixin(int power, ItemStack stack, boolean treasureAllowed, CallbackInfoReturnable<List<EnchantmentLevelEntry>> info, List<EnchantmentLevelEntry> list) {
        if (!(stack.getItem() instanceof ShieldItem)) {
            Iterator<EnchantmentLevelEntry> entryIterator = list.iterator();
            while (entryIterator.hasNext()) {
                EnchantmentLevelEntry entry = entryIterator.next();
                if (entry.enchantment == AdditionMain.STAMPEDE_ENCHANTMENT) {
                    list.remove(entry);
                    break;
                }
            }
        }
    }
}
