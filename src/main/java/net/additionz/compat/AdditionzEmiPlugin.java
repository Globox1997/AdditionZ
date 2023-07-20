package net.additionz.compat;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.handler.EmiRecipeHandler;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiStack;
import net.additionz.AdditionMain;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class AdditionzEmiPlugin implements EmiPlugin {

    // TOdo https://github.com/emilyploszaj/emi/wiki/Getting-Started-Guide
    public static final Identifier MY_SPRITE_SHEET = new Identifier("additionz", "textures/gui/emi_simplified_textures.png");
    public static final EmiStack FLETCHING_TABLE = EmiStack.of(Items.FLETCHING_TABLE);
    public static final EmiRecipeCategory FLETCHING_CATEGORY = new EmiRecipeCategory(new Identifier("additionz", "my_workstation"), FLETCHING_TABLE, new EmiTexture(MY_SPRITE_SHEET, 0, 0, 16, 16));

    @Override
    public void register(EmiRegistry emiRegistry) {
        // emiRegistry.addCategory(FLETCHING_CATEGORY);
        // emiRegistry.addWorkstation(FLETCHING_CATEGORY, FLETCHING_TABLE);
    }

}
