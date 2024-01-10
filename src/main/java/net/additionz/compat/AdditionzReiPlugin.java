package net.additionz.compat;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.DisplaySerializerRegistry;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.additionz.AdditionMain;
import net.additionz.misc.FletchingRecipe;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;

@Environment(EnvType.CLIENT)
public class AdditionzReiPlugin implements REIClientPlugin {

    public static final CategoryIdentifier<FlecthingReiDisplay> FLETCHING = CategoryIdentifier.of("minecraft", "plugins/fletching");

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new FletchingReiCategory());
        registry.addWorkstations(FLETCHING, EntryStacks.of(Blocks.FLETCHING_TABLE));
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(FletchingRecipe.class, AdditionMain.FLETCHING_RECIPE, FlecthingReiDisplay::new);
    }

    @Override
    public void registerDisplaySerializer(DisplaySerializerRegistry registry) {
        registry.register(FLETCHING, FlecthingReiDisplay.serializer());
    }

}
