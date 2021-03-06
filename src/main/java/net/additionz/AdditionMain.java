package net.additionz;

import java.util.Arrays;
import java.util.List;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.additionz.config.AdditionConfig;
import net.additionz.misc.*;
import net.additionz.network.AdditionServerPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.BinomialLootNumberProvider;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;

public class AdditionMain implements ModInitializer {

    public static AdditionConfig CONFIG = new AdditionConfig();

    public static final Enchantment BLOCK_PIERCE_ENCHANTMENT = new BlockPiercingEnchantment(Enchantment.Rarity.RARE, EnchantmentTarget.BOW, EquipmentSlot.MAINHAND);
    public static final Enchantment STAMPEDE_ENCHANTMENT = new StampedeEnchantment(Enchantment.Rarity.RARE, EnchantmentTarget.VANISHABLE, EquipmentSlot.OFFHAND);
    public static final Enchantment EAGLE_EYED_ENCHANTMENT = new EagleEyedEnchantment(Enchantment.Rarity.RARE, EnchantmentTarget.VANISHABLE, EquipmentSlot.MAINHAND);
    public static final Enchantment INACCURACY_CURSE_ENCHANTMENT = new InaccuracyCurseEnchantment(Enchantment.Rarity.RARE, EnchantmentTarget.BOW, EquipmentSlot.MAINHAND);

    public static final Item TOTEM_OF_NON_BREAKING = new Item(new Item.Settings().maxCount(1).group(ItemGroup.COMBAT).rarity(Rarity.UNCOMMON));

    public static final List<Direction> DIRECTIONS = Arrays.asList(Direction.DOWN, Direction.UP, Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH);

    @Override
    public void onInitialize() {

        // Config
        AutoConfig.register(AdditionConfig.class, JanksonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(AdditionConfig.class).getConfig();

        // Registries
        if (CONFIG.block_pearcing_enchantment)
            Registry.register(Registry.ENCHANTMENT, "additionz:block_piercing", BLOCK_PIERCE_ENCHANTMENT);
        if (CONFIG.stampede_enchantment)
            Registry.register(Registry.ENCHANTMENT, "additionz:stampede", STAMPEDE_ENCHANTMENT);
        if (CONFIG.eagle_eyed_enchantment)
            Registry.register(Registry.ENCHANTMENT, "additionz:eagle_eyed", EAGLE_EYED_ENCHANTMENT);
        if (CONFIG.inaccuracy_curse_enchantment)
            Registry.register(Registry.ENCHANTMENT, "additionz:inaccuracy_curse", INACCURACY_CURSE_ENCHANTMENT);
        if (CONFIG.totem_of_non_breaking) {
            Registry.register(Registry.ITEM, "additionz:totem_of_non_breaking", TOTEM_OF_NON_BREAKING);
            LootTableEvents.MODIFY.register((resourceManager, lootManager, id, supplier, setter) -> {
                if (id.equals(LootTables.END_CITY_TREASURE_CHEST)) {
                    LootPool pool = LootPool.builder().with(ItemEntry.builder(TOTEM_OF_NON_BREAKING).build()).rolls(BinomialLootNumberProvider.create(1, 0.8F)).build();
                    supplier.pool(pool);
                }
            });
        }

        AdditionServerPacket.init();
    }

}
