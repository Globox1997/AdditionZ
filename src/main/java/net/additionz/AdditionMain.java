package net.additionz;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.additionz.config.AdditionConfig;
import net.additionz.misc.*;
import net.additionz.network.AdditionServerPacket;
import net.fabricmc.api.ModInitializer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.util.registry.Registry;

public class AdditionMain implements ModInitializer {

    public static AdditionConfig CONFIG = new AdditionConfig();

    public static final Enchantment BLOCK_PIERCE_ENCHANTMENT = new BlockPiercingEnchantment(Enchantment.Rarity.RARE, EnchantmentTarget.BOW, EquipmentSlot.MAINHAND);
    public static final Enchantment STAMPEDE_ENCHANTMENT = new StampedeEnchantment(Enchantment.Rarity.RARE, EnchantmentTarget.VANISHABLE, EquipmentSlot.OFFHAND);

    @Override
    public void onInitialize() {

        // Config
        AutoConfig.register(AdditionConfig.class, JanksonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(AdditionConfig.class).getConfig();

        // Registries
        if (CONFIG.block_pearcing_enchantment)
            Registry.register(Registry.ENCHANTMENT, "block_piercing", BLOCK_PIERCE_ENCHANTMENT);
        if (CONFIG.stampede_enchantment)
            Registry.register(Registry.ENCHANTMENT, "stampede", STAMPEDE_ENCHANTMENT);

        AdditionServerPacket.init();
    }

}
