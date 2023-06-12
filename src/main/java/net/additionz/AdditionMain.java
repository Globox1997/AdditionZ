package net.additionz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.additionz.config.AdditionConfig;
import net.additionz.misc.*;
import net.additionz.network.AdditionServerPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.levelz.data.LevelLists;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.provider.number.BinomialLootNumberProvider;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.Direction;

public class AdditionMain implements ModInitializer {

    public static AdditionConfig CONFIG = new AdditionConfig();

    public static final Enchantment BLOCK_PIERCE_ENCHANTMENT = new BlockPiercingEnchantment(Enchantment.Rarity.RARE, EnchantmentTarget.BOW, EquipmentSlot.MAINHAND);
    public static final Enchantment STAMPEDE_ENCHANTMENT = new StampedeEnchantment(Enchantment.Rarity.RARE, EnchantmentTarget.VANISHABLE, EquipmentSlot.OFFHAND);
    public static final Enchantment EAGLE_EYED_ENCHANTMENT = new EagleEyedEnchantment(Enchantment.Rarity.RARE, EnchantmentTarget.VANISHABLE, EquipmentSlot.MAINHAND);
    public static final Enchantment INACCURACY_CURSE_ENCHANTMENT = new InaccuracyCurseEnchantment(Enchantment.Rarity.RARE, EnchantmentTarget.BOW, EquipmentSlot.MAINHAND);
    public static final Enchantment DEXTERITY_ENCHANTMENT = new DexterityEnchantment(Enchantment.Rarity.RARE, EnchantmentTarget.ARMOR_FEET, EquipmentSlot.FEET);

    public static final Item TOTEM_OF_NON_BREAKING = new Item(new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON));

    public static final List<Direction> DIRECTIONS = Arrays.asList(Direction.DOWN, Direction.UP, Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH);

    public static final TagKey<Block> PATH_BLOCKS = TagKey.of(RegistryKeys.BLOCK, new Identifier("additionz", "path_blocks"));
    public static final TagKey<Item> PASSIVE_AGE_ITEMS = TagKey.of(RegistryKeys.ITEM, new Identifier("additionz", "passive_age_items"));

    public static final RecipeType<FletchingRecipe> FLETCHING_RECIPE = RecipeType.register("fletching");
    public static final RecipeSerializer<FletchingRecipe> FLETCHING_SERIALIZER = RecipeSerializer.register("fletching", new FletchingRecipe.Serializer());
    public static ScreenHandlerType<FletchingScreenHandler> FLETCHING = new ScreenHandlerType<>(FletchingScreenHandler::new, FeatureFlags.VANILLA_FEATURES);

    public static final boolean isLevelzLoaded = FabricLoader.getInstance().isModLoaded("levelz");

    @Override
    public void onInitialize() {

        // Config
        AutoConfig.register(AdditionConfig.class, JanksonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(AdditionConfig.class).getConfig();

        // Registries
        if (CONFIG.block_pearcing_enchantment) {
            Registry.register(Registries.ENCHANTMENT, "additionz:block_piercing", BLOCK_PIERCE_ENCHANTMENT);
        }
        if (CONFIG.stampede_enchantment) {
            Registry.register(Registries.ENCHANTMENT, "additionz:stampede", STAMPEDE_ENCHANTMENT);
        }
        if (CONFIG.eagle_eyed_enchantment) {
            Registry.register(Registries.ENCHANTMENT, "additionz:eagle_eyed", EAGLE_EYED_ENCHANTMENT);
        }
        if (CONFIG.inaccuracy_curse_enchantment) {
            Registry.register(Registries.ENCHANTMENT, "additionz:inaccuracy_curse", INACCURACY_CURSE_ENCHANTMENT);
        }
        if (CONFIG.dexterity_enchantment) {
            Registry.register(Registries.ENCHANTMENT, "additionz:dexterity", DEXTERITY_ENCHANTMENT);
        }
        // Has to get registered
        Registry.register(Registries.ITEM, "additionz:totem_of_non_breaking", TOTEM_OF_NON_BREAKING);
        if (CONFIG.totem_of_non_breaking) {
            LootTableEvents.MODIFY.register((resourceManager, lootManager, id, supplier, setter) -> {
                if (id.equals(LootTables.END_CITY_TREASURE_CHEST)) {
                    LootPool pool = LootPool.builder().with(ItemEntry.builder(TOTEM_OF_NON_BREAKING).build()).rolls(BinomialLootNumberProvider.create(1, 0.8F)).build();
                    supplier.pool(pool);
                }
            });
            ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> entries.add(TOTEM_OF_NON_BREAKING));
        }
        if (CONFIG.husk_drops_sand)
            LootTableEvents.MODIFY.register((resourceManager, lootManager, id, supplier, setter) -> {
                if ("minecraft:entities/husk".equals(id.toString())) {
                    LootPool pool = LootPool.builder().with(ItemEntry.builder(Items.SAND).build()).rolls(BinomialLootNumberProvider.create(2, 0.3F)).build();
                    supplier.pool(pool);
                }
            });
        if (CONFIG.trident_buried_treasure)
            LootTableEvents.MODIFY.register((resourceManager, lootManager, id, supplier, setter) -> {
                if ("minecraft:chests/buried_treasure".equals(id.toString())) {
                    LootPool pool = LootPool.builder().with(ItemEntry.builder(Items.TRIDENT).build()).rolls(BinomialLootNumberProvider.create(1, 0.3F)).build();
                    supplier.pool(pool);
                }
            });

        AdditionServerPacket.init();

        Registry.register(Registries.SCREEN_HANDLER, "fletching", FLETCHING);
    }

    public static boolean tryUseTotemOfNonBreaking(PlayerEntity playerEntity, ItemStack itemStack) {
        if (AdditionMain.CONFIG.totem_of_non_breaking && !playerEntity.getWorld().isClient()) {
            PlayerInventory playerInventory = playerEntity.getInventory();

            for (int i = 0; i < playerInventory.size(); i++) {
                if (playerInventory.getStack(i).getItem().equals(AdditionMain.TOTEM_OF_NON_BREAKING)) {

                    if (isLevelzLoaded) {
                        ArrayList<Object> levelList = LevelLists.customItemList;
                        String string = Registries.ITEM.getId(itemStack.getItem()).toString();
                        if (!levelList.isEmpty() && levelList.contains(string)) {
                            if (!PlayerStatsManager.playerLevelisHighEnough(playerEntity, levelList, string, true)) {
                                playerEntity.sendMessage(Text.translatable("item.levelz." + levelList.get(levelList.indexOf(string) + 1) + ".tooltip", levelList.get(levelList.indexOf(string) + 2))
                                        .formatted(Formatting.RED), true);
                                return false;
                            }
                        }
                    }

                    playerInventory.getStack(i).decrement(1);
                    itemStack.setDamage((int) (itemStack.getMaxDamage() * 0.95F));
                    AdditionServerPacket.writeS2CTotemOfNonBreakingPacket((ServerPlayerEntity) playerEntity);
                    return true;
                }
            }

        }
        return false;
    }

}
