package net.additionz.data;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.additionz.AdditionMain;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

public class ExperienceLoader implements SimpleSynchronousResourceReloadListener {

    private static final Logger LOGGER = LogManager.getLogger("AdditionZ");

    @Override
    public Identifier getFabricId() {
        return new Identifier("additionz", "experience_loader");
    }

    @Override
    public void reload(ResourceManager resourceManager) {
        AdditionMain.ENTITY_EXPERIENCE_MAP.clear();
        resourceManager.findResources("entity_experience", id -> id.getPath().endsWith(".json")).forEach((id, resourceRef) -> {
            try {
                InputStream stream = resourceRef.getInputStream();
                JsonObject data = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();

                Iterator<String> iterator = data.keySet().iterator();
                while (iterator.hasNext()) {
                    String entityType = iterator.next();
                    if (Registries.ENTITY_TYPE.get(new Identifier(entityType)).toString().equals("entity.minecraft.pig")) {
                        LOGGER.info("Resource {} was not loaded cause {} is not a valid entity identifier", id.toString(), entityType);
                        continue;
                    }
                    AdditionMain.ENTITY_EXPERIENCE_MAP.put(Registries.ENTITY_TYPE.get(new Identifier(entityType)), data.get(entityType).getAsInt());

                }
            } catch (Exception e) {
                LOGGER.error("Error occurred while loading resource {}. {}", id.toString(), e.toString());
            }
        });
    }

}
