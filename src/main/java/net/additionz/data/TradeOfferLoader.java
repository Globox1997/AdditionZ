package net.additionz.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.additionz.AdditionMain;
import net.additionz.access.TradeOfferAccess;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradedItem;
import net.minecraft.village.VillagerProfession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class TradeOfferLoader implements SimpleSynchronousResourceReloadListener {

    private static final Logger LOGGER = LogManager.getLogger("AdditionZ");

    @Override
    public Identifier getFabricId() {
        return AdditionMain.identifierOf("trade_offer_loader");
    }

    @Override
    public void reload(ResourceManager resourceManager) {
        AdditionMain.TRADE_MAP.clear();

        resourceManager.findResources("villager_trades", id -> id.getPath().endsWith(".json")).forEach((id, resourceRef) -> {
            try (InputStream stream = resourceRef.getInputStream()) {
                JsonObject root = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();
                parseFile(id.toString(), root);
            } catch (Exception e) {
                LOGGER.error("Error loading villager trade file {}: {}", id, e.toString());
            }
        });
    }

    private void parseFile(String fileId, JsonObject root) {
        for (Map.Entry<String, JsonElement> professionEntry : root.entrySet()) {
            String professionKey = professionEntry.getKey();

            Optional<VillagerProfession> professionOpt = resolveProfession(professionKey);
            if (professionOpt.isEmpty()) {
                LOGGER.warn("{} – unknown profession '{}', skipping.", fileId, professionKey);
                continue;
            }
            VillagerProfession profession = professionOpt.get();

            if (!professionEntry.getValue().isJsonObject()) {
                LOGGER.warn("{} – value for profession '{}' must be a JSON object.", fileId, professionKey);
                continue;
            }

            JsonObject levelMap = professionEntry.getValue().getAsJsonObject();
            for (Map.Entry<String, JsonElement> levelEntry : levelMap.entrySet()) {
                int level;
                try {
                    level = Integer.parseInt(levelEntry.getKey());
                } catch (NumberFormatException e) {
                    LOGGER.warn("{} – invalid level key '{}' for profession '{}'.", fileId, levelEntry.getKey(), professionKey);
                    continue;
                }

                if (!levelEntry.getValue().isJsonArray()) {
                    LOGGER.warn("{} – trades for {}/{} must be a JSON array.", fileId, professionKey, level);
                    continue;
                }

                JsonArray tradesArray = levelEntry.getValue().getAsJsonArray();
                for (JsonElement tradeElement : tradesArray) {
                    if (!tradeElement.isJsonObject()) continue;
                    parseTrade(fileId, professionKey, profession, level, tradeElement.getAsJsonObject());
                }
            }
        }
    }

    private void parseTrade(String fileId, String professionKey, VillagerProfession profession, int level, JsonObject obj) {
        try {
            String type = requireString(obj, "type");
            String itemId = requireString(obj, "item");
            int price = requireInt(obj, "price");
            int count = obj.has("count") ? obj.get("count").getAsInt() : 1;
            int maxUses = obj.has("maxUses") ? obj.get("maxUses").getAsInt() : 12;
            float priceMultiplier = obj.has("priceMultiplier") ? obj.get("priceMultiplier").getAsInt() : 0.05f;
            int experience = obj.has("experience") ? obj.get("experience").getAsInt() : 1;
            int playerExperience = obj.has("playerExperience") ? obj.get("playerExperience").getAsInt() : 0;

            if (!type.equals("buy") && !type.equals("sell")) {
                LOGGER.warn("[Addition] {} – unknown trade type '{}' for {}/{}, skipping.", fileId, type, professionKey, level);
                return;
            }

            Item item = Registries.ITEM.get(Identifier.of(itemId));
            if (item == Items.AIR) {
                LOGGER.warn("[Addition] {} – unknown item '{}' for {}/{}, skipping.", fileId, itemId, professionKey, level);
                return;
            }

            TradeOfferData data = new TradeOfferData(type, item, count, price, maxUses, priceMultiplier, experience, playerExperience);
            AdditionMain.TRADE_MAP.computeIfAbsent(profession, p -> new HashMap<>()).computeIfAbsent(level, l -> new ArrayList<>()).add(data);

        } catch (IllegalArgumentException e) {
            LOGGER.warn("[Addition] {} – malformed trade entry for {}/{}: {}", fileId, professionKey, level, e.getMessage());
        }
    }

    public static List<TradeOffer> getTradesForProfession(VillagerProfession profession, int level) {
        Map<Integer, List<TradeOfferData>> levelMap = AdditionMain.TRADE_MAP.get(profession);
        if (levelMap == null) {
            return Collections.emptyList();
        }
        List<TradeOfferData> dataList = levelMap.get(level);
        if (dataList == null) {
            return Collections.emptyList();
        }
        List<TradeOffer> offers = new ArrayList<>();
        for (TradeOfferData tradeOfferData : dataList) {
            offers.add(tradeOfferData.toTradeOffer());
        }
        return offers;
    }

    private static Optional<VillagerProfession> resolveProfession(String name) {
        return Registries.VILLAGER_PROFESSION.getOrEmpty(Identifier.of("minecraft", name));
    }

    private static String requireString(JsonObject obj, String key) {
        if (!obj.has(key)) {
            throw new IllegalArgumentException("Missing required field '" + key + "'");
        }
        return obj.get(key).getAsString();
    }

    private static int requireInt(JsonObject obj, String key) {
        if (!obj.has(key)) {
            throw new IllegalArgumentException("Missing required field '" + key + "'");
        }
        return obj.get(key).getAsInt();
    }

    public static class TradeOfferData {

        public final String type;
        public final Item item;
        public final int count;
        public final int price;
        public final int maxUses;
        public final float priceMultiplier;
        public final int experience;
        public final int playerExperience;

        public TradeOfferData(String type, Item item, int count, int price, int maxUses, float priceMultiplier, int experience, int playerExperience) {
            this.type = type;
            this.item = item;
            this.count = count;
            this.price = price;
            this.maxUses = maxUses;
            this.priceMultiplier = priceMultiplier;
            this.experience = experience;
            this.playerExperience = playerExperience;
        }

        public TradeOffer toTradeOffer() {
            if (type.equals("buy")) {
                TradedItem playerInput = new TradedItem(item, count);
                ItemStack emeraldOut = new ItemStack(Items.EMERALD, price);
                TradeOffer tradeOffer = new TradeOffer(playerInput, emeraldOut, maxUses, experience, priceMultiplier);
                ((TradeOfferAccess) tradeOffer).setPlayerExperience(playerExperience);
                return tradeOffer;
            } else {
                TradedItem emeraldInput = new TradedItem(Items.EMERALD, price);
                ItemStack itemOut = new ItemStack(item, count);
                TradeOffer tradeOffer = new TradeOffer(emeraldInput, itemOut, maxUses, experience, priceMultiplier);
                ((TradeOfferAccess) tradeOffer).setPlayerExperience(playerExperience);
                return tradeOffer;
            }
        }
    }

}
