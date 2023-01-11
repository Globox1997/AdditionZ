package net.additionz.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.additionz.AdditionMain;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.TridentItem;

@Mixin(TridentItem.class)
public abstract class TridentItemMixin extends Item {

    public TridentItemMixin(Settings settings) {
        super(settings);
    }

    @Override
    public boolean canRepair(ItemStack stack, ItemStack ingredient) {
        if (AdditionMain.CONFIG.repair_trident)
            return ingredient.isOf(Items.PRISMARINE_SHARD) || super.canRepair(stack, ingredient);
        else
            return super.canRepair(stack, ingredient);
    }

}
