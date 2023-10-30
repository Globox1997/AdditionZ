package net.additionz.compat;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiStack;
import net.additionz.AdditionMain;
import net.additionz.misc.FletchingRecipe;
import net.additionz.misc.FletchingScreen;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.util.Identifier;

public class AdditionzEmiPlugin implements EmiPlugin {

    public static final EmiStack FLETCHING_TABLE = EmiStack.of(Items.FLETCHING_TABLE);
    public static final EmiRecipeCategory FLETCHING_CATEGORY = new EmiRecipeCategory(new Identifier("additionz", "fletching"), FLETCHING_TABLE,
            new EmiTexture(FletchingScreen.TEXTURE, 13, 166, 16, 16));

    @Override
    public void register(EmiRegistry emiRegistry) {
        emiRegistry.addCategory(FLETCHING_CATEGORY);
        emiRegistry.addWorkstation(FLETCHING_CATEGORY, FLETCHING_TABLE);
        RecipeManager manager = emiRegistry.getRecipeManager();
        for (FletchingRecipe recipe : manager.listAllOfType(AdditionMain.FLETCHING_RECIPE)) {
            emiRegistry.addRecipe(new FletchingEmiRecipe(recipe));
        }
    }

}
