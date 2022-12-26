package net.additionz.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.additionz.AdditionMain;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.recipebook.RecipeBookGroup;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;

@Environment(EnvType.CLIENT)
@Mixin(ClientRecipeBook.class)
public class ClientRecipeBookMixin {

    @Inject(method = "getGroupForRecipe", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void getGroupForRecipeMixin(Recipe<?> recipe, CallbackInfoReturnable<RecipeBookGroup> info, RecipeType<?> recipeType) {
        if (recipeType == AdditionMain.FLETCHING_RECIPE) {
            info.setReturnValue(RecipeBookGroup.UNKNOWN);
        }
    }
}
