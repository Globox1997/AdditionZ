package net.additionz.compat;

import java.util.List;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.additionz.misc.FletchingRecipe;
import net.additionz.misc.FletchingScreen;
import net.minecraft.util.Identifier;

public class FletchingEmiRecipe implements EmiRecipe {
    private final Identifier id;
    private final List<EmiIngredient> input;
    private final List<EmiStack> output;

    public FletchingEmiRecipe(FletchingRecipe recipe) {
        this.id = recipe.getId();
        this.input = List.of(EmiIngredient.of(recipe.getIngredients().get(0)), EmiIngredient.of(recipe.getIngredients().get(1)), EmiIngredient.of(recipe.getIngredients().get(2)),
                EmiIngredient.of(recipe.getIngredients().get(3)));
        this.output = List.of(EmiStack.of(recipe.getOutput(null)));
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return AdditionzEmiPlugin.FLETCHING_CATEGORY;
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return input;
    }

    @Override
    public List<EmiStack> getOutputs() {
        return output;
    }

    @Override
    public int getDisplayWidth() {
        return 126;
    }

    @Override
    public int getDisplayHeight() {
        return 62;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(new EmiTexture(FletchingScreen.TEXTURE, 0, 166, 13, 13), 28, 25);
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 70, 23);
        widgets.addSlot(input.get(2), 5, 5);
        widgets.addSlot(input.get(1), 5, 23);
        widgets.addSlot(input.get(0), 5, 41);
        widgets.addSlot(input.get(3), 45, 23);
        widgets.addSlot(output.get(0), 101, 23).recipeContext(this);
    }

}
