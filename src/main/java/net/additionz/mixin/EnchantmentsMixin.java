package net.additionz.mixin;

import net.additionz.AdditionMain;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.ItemTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Enchantments.class)
public class EnchantmentsMixin {

    @Inject(method = "bootstrap", at = @At("TAIL"))
    private static void bootstrapMixin(Registerable<Enchantment> registry, CallbackInfo info) {
        register(registry, AdditionMain.BLOCK_PIERCE_ENCHANTMENT, Enchantment.builder(Enchantment.definition(registry.getRegistryLookup(RegistryKeys.ITEM).getOrThrow(ItemTags.BOW_ENCHANTABLE), 5, 3,
                Enchantment.leveledCost(25, 25), Enchantment.leveledCost(75, 25), 8, AttributeModifierSlot.MAINHAND)));

        register(registry, AdditionMain.EAGLE_EYED_ENCHANTMENT, Enchantment.builder(Enchantment.definition(registry.getRegistryLookup(RegistryKeys.ITEM).getOrThrow(AdditionMain.SPYGLASSES), 5, 1,
                Enchantment.constantCost(1), Enchantment.constantCost(41), 8, AttributeModifierSlot.MAINHAND)));

        register(registry, AdditionMain.STAMPEDE_ENCHANTMENT, Enchantment.builder(Enchantment.definition(registry.getRegistryLookup(RegistryKeys.ITEM).getOrThrow(ConventionalItemTags.SHIELD_TOOLS),
                5, 3, Enchantment.leveledCost(25, 25), Enchantment.leveledCost(75, 25), 8, AttributeModifierSlot.MAINHAND)));

        register(registry, AdditionMain.INACCURACY_CURSE_ENCHANTMENT,
                Enchantment.builder(Enchantment.definition(registry.getRegistryLookup(RegistryKeys.ITEM).getOrThrow(ConventionalItemTags.RANGED_WEAPON_TOOLS), 5, 3, Enchantment.leveledCost(25, 25),
                        Enchantment.leveledCost(75, 25), 8, AttributeModifierSlot.MAINHAND)));
    }

    @Shadow
    private static void register(Registerable<Enchantment> registry, RegistryKey<Enchantment> key, Enchantment.Builder builder) {
    }

}
