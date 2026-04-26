package net.additionz.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.additionz.AdditionMain;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;

public class ExperienceLoader implements SimpleSynchronousResourceReloadListener {

    private static final Logger LOGGER = LogManager.getLogger("AdditionZ");

    @Override
    public Identifier getFabricId() {
        return AdditionMain.identifierOf("experience_loader");
    }

    @Override
    public void reload(ResourceManager resourceManager) {
        AdditionMain.ENTITY_EXPERIENCE_MAP.clear();
        resourceManager.findResources("entity_experience", id -> id.getPath().endsWith(".json")).forEach((id, resourceRef) -> {
            try {
                InputStream stream = resourceRef.getInputStream();
                JsonObject data = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();

                for (String entityType : data.keySet()) {
                    if (Registries.ENTITY_TYPE.get(Identifier.of(entityType)).toString().equals("entity.minecraft.pig")) {
                        LOGGER.info("Resource {} was not loaded cause {} is not a valid entity identifier", id.toString(), entityType);
                        continue;
                    }
                    AdditionMain.ENTITY_EXPERIENCE_MAP.put(Registries.ENTITY_TYPE.get(Identifier.of(entityType)), data.get(entityType).getAsInt());

                }
            } catch (Exception e) {
                LOGGER.error("Error occurred while loading resource {}. {}", id.toString(), e.toString());
            }
        });
        AdditionMain.BREEDING_EXPERIENCE_MAP.clear();
        resourceManager.findResources("breeding_experience", id -> id.getPath().endsWith(".json")).forEach((id, resourceRef) -> {
            try {
                InputStream stream = resourceRef.getInputStream();
                JsonObject data = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();

                for (String entityType : data.keySet()) {
                    if (Registries.ENTITY_TYPE.get(Identifier.of(entityType)).toString().equals("entity.minecraft.pig")) {
                        LOGGER.info("Resource {} was not loaded cause {} is not a valid entity identifier", id.toString(), entityType);
                        continue;
                    }
                    AdditionMain.BREEDING_EXPERIENCE_MAP.put(Registries.ENTITY_TYPE.get(Identifier.of(entityType)), data.get(entityType).getAsInt());

                }
            } catch (Exception e) {
                LOGGER.error("Error occurred while loading resource {}. {}", id.toString(), e.toString());
            }
        });
        AdditionMain.FISHING_EXPERIENCE_MAP.clear();
        resourceManager.findResources("fishing_experience", id -> id.getPath().endsWith(".json")).forEach((id, resourceRef) -> {
            try {
                InputStream stream = resourceRef.getInputStream();
                JsonObject data = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();

                for (String itemId : data.keySet()) {
                    if (Registries.ITEM.get(Identifier.of(itemId)).getDefaultStack().isEmpty()) {
                        LOGGER.info("Resource {} was not loaded cause {} is not a valid item identifier", id.toString(), itemId);
                        continue;
                    }
                    AdditionMain.FISHING_EXPERIENCE_MAP.put(Registries.ITEM.get(Identifier.of(itemId)), data.get(itemId).getAsInt());

                }
            } catch (Exception e) {
                LOGGER.error("Error occurred while loading resource {}. {}", id.toString(), e.toString());
            }
        });
    }

}
