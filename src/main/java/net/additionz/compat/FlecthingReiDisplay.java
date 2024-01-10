package net.additionz.compat;

import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.additionz.misc.FletchingRecipe;
import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class FlecthingReiDisplay extends BasicDisplay {

    public FlecthingReiDisplay(FletchingRecipe recipe) {
        this(EntryIngredients.ofIngredients(recipe.getIngredients()), Collections.singletonList(EntryIngredients.of(recipe.getOutput(BasicDisplay.registryAccess()))),
                Optional.ofNullable(recipe.getId()));
    }

    public FlecthingReiDisplay(List<EntryIngredient> inputs, List<EntryIngredient> outputs, Optional<Identifier> location) {
        super(inputs, outputs, location);
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return AdditionzReiPlugin.FLETCHING;
    }

    public static BasicDisplay.Serializer<FlecthingReiDisplay> serializer() {
        return BasicDisplay.Serializer.ofSimple(FlecthingReiDisplay::new);
    }
}
