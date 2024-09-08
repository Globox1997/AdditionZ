package net.additionz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.additionz.block.ChunkLoaderBlock;
import net.additionz.block.entity.ChunkLoaderEntity;
import net.additionz.block.screen.ChunkLoaderScreenHandler;
import net.additionz.config.AdditionConfig;
import net.additionz.data.ExperienceLoader;
import net.additionz.item.*;
import net.additionz.misc.*;
import net.additionz.network.AdditionServerPacket;
import net.additionz.network.packet.ChunkLoaderBlockPacket;
import net.additionz.network.packet.TotemPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.loader.api.FabricLoader;
import net.levelz.data.LevelLists;
import net.levelz.stats.PlayerStatsManager;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
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
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.TradedItem;

public class AdditionMain implements ModInitializer {

    public static AdditionConfig CONFIG = new AdditionConfig();

    public static final Map<EntityType<?>, Integer> ENTITY_EXPERIENCE_MAP = new HashMap<EntityType<?>, Integer>();

    public static final TagKey<Block> PATH_BLOCKS = TagKey.of(RegistryKeys.BLOCK, Identifier.of("additionz", "path_blocks"));
    public static final TagKey<Item> PASSIVE_AGE_ITEMS = TagKey.of(RegistryKeys.ITEM, Identifier.of("additionz", "passive_age_items"));
    public static final TagKey<Item> SPYGLASSES = TagKey.of(RegistryKeys.ITEM, Identifier.of("minecraft", "spyglasses"));

    public static final RegistryKey<Enchantment> BLOCK_PIERCE_ENCHANTMENT = RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of("additionz", "block_piercing"));
    // new BlockPiercingEnchantment();
    public static final RegistryKey<Enchantment> STAMPEDE_ENCHANTMENT = RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of("additionz", "stampede"));
    // new StampedeEnchantment();
    public static final RegistryKey<Enchantment> EAGLE_EYED_ENCHANTMENT = RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of("additionz", "eagle_eyed"));
    // new EagleEyedEnchantment();
    public static final RegistryKey<Enchantment> INACCURACY_CURSE_ENCHANTMENT = RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of("additionz", "inaccuracy_curse"));
    // new InaccuracyCurseEnchantment();
    public static final RegistryKey<Enchantment> DEXTERITY_ENCHANTMENT = RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of("additionz", "dexterity"));
    // new DexterityEnchantment();

    public static final Item TOTEM_OF_NON_BREAKING = new Item(new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON));
    public static final Item TELEPORT_SCROLL = new TeleportScrollItem(new Item.Settings().maxCount(16));
    public static final Item TELEPORT_POTION = new TeleportPotion(new Item.Settings().maxCount(16));

    public static final Block CHUNK_LOADER = new ChunkLoaderBlock(AbstractBlock.Settings.copy(Blocks.LODESTONE).nonOpaque().pistonBehavior(PistonBehavior.IGNORE));
    public static BlockEntityType<ChunkLoaderEntity> CHUNK_LOADER_ENTITY;
    public static final ScreenHandlerType<ChunkLoaderScreenHandler> CHUNK_LOADER_SCREEN_HANDLER = new ExtendedScreenHandlerType<ChunkLoaderScreenHandler, ChunkLoaderBlockPacket>(
            (syncId, playerInventory, buf) -> new ChunkLoaderScreenHandler(syncId, playerInventory, buf), ChunkLoaderBlockPacket.PACKET_CODEC);

    public static final List<Direction> DIRECTIONS = Arrays.asList(Direction.DOWN, Direction.UP, Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH);

    public static final RecipeType<FletchingRecipe> FLETCHING_RECIPE = RecipeType.register("fletching");
    public static final RecipeSerializer<FletchingRecipe> FLETCHING_SERIALIZER = RecipeSerializer.register("fletching", new FletchingRecipe.Serializer());
    public static ScreenHandlerType<FletchingScreenHandler> FLETCHING = new ScreenHandlerType<>(FletchingScreenHandler::new, FeatureFlags.VANILLA_FEATURES);

    public static final Identifier AGE_INFO = Identifier.of("additionz", "age_info");

    public static final boolean isLevelzLoaded = FabricLoader.getInstance().isModLoaded("levelz");
    public static final boolean isBackSlotLoaded = FabricLoader.getInstance().isModLoaded("backslot");

    @Override
    public void onInitialize() {

        // Config
        AutoConfig.register(AdditionConfig.class, GsonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(AdditionConfig.class).getConfig();

        // Registries
        // Enchantments
        // Registry.register(Registries.ENCHANTMENT, "additionz:block_piercing", BLOCK_PIERCE_ENCHANTMENT);
        // Registry.register(Registries.ENCHANTMENT, "additionz:stampede", STAMPEDE_ENCHANTMENT);
        // Registry.register(Registries.ENCHANTMENT, "additionz:eagle_eyed", EAGLE_EYED_ENCHANTMENT);
        // Registry.register(Registries.ENCHANTMENT, "additionz:inaccuracy_curse", INACCURACY_CURSE_ENCHANTMENT);
        // Registry.register(Registries.ENCHANTMENT, "additionz:dexterity", DEXTERITY_ENCHANTMENT);

        // Has to get registered
        Registry.register(Registries.ITEM, "additionz:totem_of_non_breaking", TOTEM_OF_NON_BREAKING);
        if (CONFIG.totem_of_non_breaking) {
            LootTableEvents.MODIFY.register((key, tableBuilder, source) -> {
                if (key.equals(LootTables.END_CITY_TREASURE_CHEST)) {
                    LootPool pool = LootPool.builder().with(ItemEntry.builder(TOTEM_OF_NON_BREAKING).build()).rolls(BinomialLootNumberProvider.create(1, 0.8F)).build();
                    tableBuilder.pool(pool);
                }
            });
            ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> entries.add(TOTEM_OF_NON_BREAKING));
        }
        Registry.register(Registries.ITEM, "additionz:teleport_scroll", TELEPORT_SCROLL);
        if (CONFIG.teleport_scroll) {
            LootTableEvents.MODIFY.register((key, tableBuilder, source) -> {
                if (key.equals(LootTables.END_CITY_TREASURE_CHEST)) {
                    LootPool pool = LootPool.builder().with(ItemEntry.builder(TELEPORT_SCROLL).build()).rolls(BinomialLootNumberProvider.create(1, 0.2F)).build();
                    tableBuilder.pool(pool);
                }
            });
            TradeOfferHelper.registerWanderingTraderOffers(1, factories -> {
                factories.add(new SimpleTradeFactory(new TradeOffer(new TradedItem(Items.WRITABLE_BOOK), Optional.of(new TradedItem(Items.EMERALD, 24)), new ItemStack(TELEPORT_SCROLL), 1, 1, 0.0F)));
            });
            ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> entries.add(TELEPORT_SCROLL));
        }
        Registry.register(Registries.ITEM, "additionz:teleport_potion", TELEPORT_POTION);
        if (CONFIG.teleport_potion) {
            TradeOfferHelper.registerWanderingTraderOffers(1, factories -> {
                factories.add(new SimpleTradeFactory(new TradeOffer(new TradedItem(Items.DRAGON_BREATH), Optional.of(new TradedItem(Items.EMERALD, 42)), new ItemStack(TELEPORT_POTION), 1, 1, 0.0F)));
            });
            ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(entries -> entries.add(TELEPORT_POTION));
        }
        Registry.register(Registries.BLOCK, "additionz:chunk_loader", CHUNK_LOADER);
        Registry.register(Registries.ITEM, "additionz:chunk_loader", new BlockItem(CHUNK_LOADER, new Item.Settings()));
        CHUNK_LOADER_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, "additionz:chunk_loader_entity", BlockEntityType.Builder.create(ChunkLoaderEntity::new, CHUNK_LOADER).build(null));
        if (CONFIG.chunk_loader) {
            ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(entries -> entries.add(CHUNK_LOADER));
        }
        if (CONFIG.husk_drops_sand) {
            LootTableEvents.MODIFY.register((key, tableBuilder, source) -> {
                if ("minecraft:entities/husk".equals(key.getValue().toString())) {
                    LootPool pool = LootPool.builder().with(ItemEntry.builder(Items.SAND).build()).rolls(BinomialLootNumberProvider.create(2, 0.9F)).build();
                    tableBuilder.pool(pool);
                }
            });
        }
        if (CONFIG.trident_buried_treasure)
            LootTableEvents.MODIFY.register((key, tableBuilder, source) -> {
                if ("minecraft:chests/buried_treasure".equals(key.getValue().toString())) {
                    LootPool pool = LootPool.builder().with(ItemEntry.builder(Items.TRIDENT).build()).rolls(BinomialLootNumberProvider.create(1, 0.3F)).build();
                    tableBuilder.pool(pool);
                }
            });

        AdditionServerPacket.init();

        Registry.register(Registries.SCREEN_HANDLER, "fletching", FLETCHING);
        Registry.register(Registries.SCREEN_HANDLER, "additionz:chunk_loader", CHUNK_LOADER_SCREEN_HANDLER);

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new ExperienceLoader());
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
                    ServerPlayNetworking.send((ServerPlayerEntity) playerEntity, new TotemPacket());
                    return true;
                }
            }

        }
        return false;
    }

    private class SimpleTradeFactory implements TradeOffers.Factory {
        private final TradeOffer offer;

        public SimpleTradeFactory(TradeOffer offer) {
            this.offer = offer;
        }

        @Override
        public TradeOffer create(Entity entity, Random random) {
            return this.offer;
        }
    }

}
