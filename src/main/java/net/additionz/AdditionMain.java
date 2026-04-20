package net.additionz;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.additionz.block.ChunkLoaderBlock;
import net.additionz.block.entity.ChunkLoaderEntity;
import net.additionz.block.screen.ChunkLoaderScreenHandler;
import net.additionz.config.AdditionConfig;
import net.additionz.data.ExperienceLoader;
import net.additionz.item.TeleportPotion;
import net.additionz.item.TeleportScrollItem;
import net.additionz.misc.FletchingRecipe;
import net.additionz.misc.FletchingScreenHandler;
import net.additionz.network.AdditionServerPacket;
import net.additionz.network.packet.ChunkLoaderBlockPacket;
import net.additionz.network.packet.TotemPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.loader.api.FabricLoader;
import net.levelz.access.LevelManagerAccess;
import net.levelz.level.LevelManager;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;
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
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.TradedItem;

import java.util.*;

public class AdditionMain implements ModInitializer {

    public static AdditionConfig CONFIG = new AdditionConfig();

    public static final Map<EntityType<?>, Integer> ENTITY_EXPERIENCE_MAP = new HashMap<>();

    public static final TagKey<Block> PATH_BLOCKS = TagKey.of(RegistryKeys.BLOCK, identifierOf("path_blocks"));
    public static final TagKey<Item> PASSIVE_AGE_ITEMS = TagKey.of(RegistryKeys.ITEM, identifierOf("passive_age_items"));
    public static final TagKey<Item> SPYGLASSES = TagKey.of(RegistryKeys.ITEM, Identifier.of("minecraft", "spyglasses"));

    public static final Identifier BLOCK_PIERCE = identifierOf("block_pierce");
    public static final RegistryKey<Enchantment> BLOCK_PIERCE_ENCHANTMENT = RegistryKey.of(RegistryKeys.ENCHANTMENT, BLOCK_PIERCE);

    public static final Identifier STAMPEDE = identifierOf("stampede");
    public static final RegistryKey<Enchantment> STAMPEDE_ENCHANTMENT = RegistryKey.of(RegistryKeys.ENCHANTMENT, STAMPEDE);

    public static final Identifier EAGLE_EYED = identifierOf("eagle_eyed");
    public static final RegistryKey<Enchantment> EAGLE_EYED_ENCHANTMENT = RegistryKey.of(RegistryKeys.ENCHANTMENT, EAGLE_EYED);

    public static final Identifier INACCURACY_CURSE = identifierOf("inaccuracy_curse");
    public static final RegistryKey<Enchantment> INACCURACY_CURSE_ENCHANTMENT = RegistryKey.of(RegistryKeys.ENCHANTMENT, INACCURACY_CURSE);


    public static final Item TOTEM_OF_NON_BREAKING = new Item(new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON));
    public static final Item TELEPORT_SCROLL = new TeleportScrollItem(new Item.Settings().maxCount(16));
    public static final Item TELEPORT_POTION = new TeleportPotion(new Item.Settings().maxCount(16));

    public static final Block CHUNK_LOADER = new ChunkLoaderBlock(AbstractBlock.Settings.copy(Blocks.LODESTONE).nonOpaque().pistonBehavior(PistonBehavior.IGNORE));
    public static BlockEntityType<ChunkLoaderEntity> CHUNK_LOADER_ENTITY;
    public static final ScreenHandlerType<ChunkLoaderScreenHandler> CHUNK_LOADER_SCREEN_HANDLER = new ExtendedScreenHandlerType<>(
            ChunkLoaderScreenHandler::new, ChunkLoaderBlockPacket.PACKET_CODEC);

    public static final List<Direction> DIRECTIONS = Arrays.asList(Direction.DOWN, Direction.UP, Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH);

    public static final RecipeType<FletchingRecipe> FLETCHING_RECIPE = RecipeType.register("fletching");
    public static final RecipeSerializer<FletchingRecipe> FLETCHING_SERIALIZER = RecipeSerializer.register("fletching", new FletchingRecipe.Serializer());
    public static ScreenHandlerType<FletchingScreenHandler> FLETCHING = new ScreenHandlerType<>(FletchingScreenHandler::new, FeatureFlags.VANILLA_FEATURES);

    public static final RegistryEntry<EntityAttribute> GENERIC_RANGE_ATTACK_DAMAGE = Registry.registerReference(Registries.ATTRIBUTE, Identifier.ofVanilla("generic.range_attack_damage"),
            new ClampedEntityAttribute("attribute.name.generic.range_attack_damage", 2.0, 0.0, 2048.0));

    public static final Identifier AGE_INFO = identifierOf("age_info");

    public static final boolean isLevelzLoaded = FabricLoader.getInstance().isModLoaded("levelz");
    public static final boolean isBackSlotLoaded = FabricLoader.getInstance().isModLoaded("backslot");

    public static Identifier identifierOf(String name) {
        return Identifier.of("additionz", name);
    }

    @Override
    public void onInitialize() {

        // Config
        AutoConfig.register(AdditionConfig.class, GsonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(AdditionConfig.class).getConfig();

        // Has to get registered
        Registry.register(Registries.ITEM, identifierOf("totem_of_non_breaking"), TOTEM_OF_NON_BREAKING);
        if (CONFIG.totem_of_non_breaking) {
            LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
                if (key.equals(LootTables.END_CITY_TREASURE_CHEST)) {
                    LootPool pool = LootPool.builder().with(ItemEntry.builder(TOTEM_OF_NON_BREAKING).build()).rolls(BinomialLootNumberProvider.create(1, 0.8F)).build();
                    tableBuilder.pool(pool);
                }
            });
            ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> entries.add(TOTEM_OF_NON_BREAKING));
        }
        Registry.register(Registries.ITEM, identifierOf("teleport_scroll"), TELEPORT_SCROLL);
        if (CONFIG.teleport_scroll) {
            net.fabricmc.fabric.api.loot.v3.LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
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
        Registry.register(Registries.ITEM, identifierOf("teleport_potion"), TELEPORT_POTION);
        if (CONFIG.teleport_potion) {
            TradeOfferHelper.registerWanderingTraderOffers(1, factories -> {
                factories.add(new SimpleTradeFactory(new TradeOffer(new TradedItem(Items.DRAGON_BREATH), Optional.of(new TradedItem(Items.EMERALD, 42)), new ItemStack(TELEPORT_POTION), 1, 1, 0.0F)));
            });
            ItemGroupEvents.modifyEntriesEvent(ItemGroups.FOOD_AND_DRINK).register(entries -> entries.add(TELEPORT_POTION));
        }
        Registry.register(Registries.BLOCK, identifierOf("chunk_loader"), CHUNK_LOADER);
        Registry.register(Registries.ITEM, identifierOf("chunk_loader"), new BlockItem(CHUNK_LOADER, new Item.Settings()));
        CHUNK_LOADER_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, identifierOf("chunk_loader_entity"), BlockEntityType.Builder.create(ChunkLoaderEntity::new, CHUNK_LOADER).build(null));
        if (CONFIG.chunk_loader) {
            ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(entries -> entries.add(CHUNK_LOADER));
        }
        if (CONFIG.husk_drops_sand) {
            LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
                if ("minecraft:entities/husk".equals(key.getValue().toString())) {
                    LootPool pool = LootPool.builder().with(ItemEntry.builder(Items.SAND).build()).rolls(BinomialLootNumberProvider.create(2, 0.9F)).build();
                    tableBuilder.pool(pool);
                }
            });
        }
        if (CONFIG.trident_buried_treasure)
            LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
                if ("minecraft:chests/buried_treasure".equals(key.getValue().toString())) {
                    LootPool pool = LootPool.builder().with(ItemEntry.builder(Items.TRIDENT).build()).rolls(BinomialLootNumberProvider.create(1, 0.3F)).build();
                    tableBuilder.pool(pool);
                }
            });

        AdditionServerPacket.init();

        Registry.register(Registries.SCREEN_HANDLER, "fletching", FLETCHING);
        Registry.register(Registries.SCREEN_HANDLER, identifierOf("chunk_loader"), CHUNK_LOADER_SCREEN_HANDLER);

        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new ExperienceLoader());
    }

    public static boolean tryUseTotemOfNonBreaking(PlayerEntity playerEntity, ItemStack itemStack) {
        if (AdditionMain.CONFIG.totem_of_non_breaking && !playerEntity.getWorld().isClient()) {
            PlayerInventory playerInventory = playerEntity.getInventory();

            for (int i = 0; i < playerInventory.size(); i++) {
                if (playerInventory.getStack(i).getItem().equals(AdditionMain.TOTEM_OF_NON_BREAKING)) {
                    if (isLevelzLoaded) {
                        LevelManager levelManager = ((LevelManagerAccess) playerEntity).getLevelManager();
                        if (!levelManager.hasRequiredItemLevel(itemStack.getItem())) {
                            return false;
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

    private record SimpleTradeFactory(TradeOffer offer) implements TradeOffers.Factory {

        @Override
        public TradeOffer create(Entity entity, Random random) {
            return this.offer;
        }
    }

}
