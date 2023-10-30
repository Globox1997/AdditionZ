package net.additionz.misc;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

import net.additionz.AdditionMain;
import net.minecraft.block.Blocks;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class FletchingRecipe implements Recipe<Inventory> {

    final Ingredient bottom;
    final Ingredient middle;
    final Ingredient top;
    final ItemStack addition;
    final ItemStack result;
    private final Identifier id;

    public FletchingRecipe(Identifier id, Ingredient bottom, Ingredient middle, Ingredient top, @Nullable ItemStack addition, ItemStack result) {
        this.id = id;
        this.top = top;
        this.middle = middle;
        this.bottom = bottom;
        this.addition = addition;
        this.result = result;
    }

    private static boolean areItemStackEqual(ItemStack left, ItemStack right) {
        if (left.isEmpty() && right.isEmpty()) {
            return false;
        }
        if (left.isEmpty() || right.isEmpty()) {
            return false;
        }
        if (!left.isOf(right.getItem())) {
            return false;
        }
        if (left.getNbt() == null && right.getNbt() != null) {
            return false;
        }
        return !left.hasNbt() || left.getNbt() == null || left.getNbt().equals(right.getNbt());
    }

    @Override
    public boolean matches(Inventory inventory, World world) {
        if (!this.addition.isEmpty() && !areItemStackEqual(this.addition, inventory.getStack(3))) {
            return false;
        }

        return this.bottom.test(inventory.getStack(2)) && this.middle.test(inventory.getStack(1)) && this.top.test(inventory.getStack(0));
    }

    @Override
    public ItemStack craft(Inventory inventory, DynamicRegistryManager dynamicRegistryManager) {
        ItemStack itemStack = this.result.copy();
        NbtCompound nbtCompound = inventory.getStack(1).getNbt();
        if (nbtCompound != null) {
            itemStack.setNbt(nbtCompound.copy());
        }
        return itemStack;
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        DefaultedList<Ingredient> ingredients = DefaultedList.ofSize(4);
        ingredients.add(bottom);
        ingredients.add(middle);
        ingredients.add(top);
        ingredients.add(Ingredient.ofStacks(addition));
        return ingredients;
    }

    @Override
    public ItemStack getOutput(DynamicRegistryManager dynamicRegistryManager) {
        return this.result;
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(Blocks.FLETCHING_TABLE);
    }

    @Override
    public boolean isIgnoredInRecipeBook() {
        return true;
    }

    @Override
    public Identifier getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return AdditionMain.FLETCHING_SERIALIZER;
    }

    @Override
    public RecipeType<?> getType() {
        return AdditionMain.FLETCHING_RECIPE;
    }

    // Used by ClientRecipeBook
    @Override
    public boolean isEmpty() {
        if (!this.addition.isEmpty()) {
            return false;
        }
        return Stream.of(this.bottom, this.middle, this.top).anyMatch(ingredient -> ingredient.getMatchingStacks().length == 0);
    }

    public boolean hasAddition() {
        return !this.addition.isEmpty();
    }

    private static ItemStack outputFromJson(JsonObject json) {
        Item item = ShapedRecipe.getItem(json);
        int i = JsonHelper.getInt(json, "count", 1);
        if (i < 1) {
            throw new JsonSyntaxException("Invalid output count: " + i);
        }
        ItemStack itemStack = new ItemStack(item, i);
        NbtCompound nbtCompound = new NbtCompound();

        if (json.has("data")) {
            try {
                nbtCompound = new StringNbtReader(new StringReader(json.get("data").getAsString())).parseCompound();
            } catch (CommandSyntaxException e) {
                e.printStackTrace();
                throw new JsonParseException("Failed to load fletching recipe with data usage");
            }
        }
        itemStack.setNbt(nbtCompound);
        return itemStack;
    }

    public static class Serializer implements RecipeSerializer<FletchingRecipe> {
        @Override
        public FletchingRecipe read(Identifier identifier, JsonObject jsonObject) {
            Ingredient topIngredient = Ingredient.fromJson(JsonHelper.getObject(jsonObject, "top"));
            Ingredient middleIngredient = Ingredient.fromJson(JsonHelper.getObject(jsonObject, "middle"));
            Ingredient bottomIngredient = Ingredient.fromJson(JsonHelper.getObject(jsonObject, "bottom"));
            ItemStack additionItemStack = new ItemStack(Items.AIR);
            if (JsonHelper.hasElement(jsonObject, "addition"))
                additionItemStack = outputFromJson(JsonHelper.getObject(jsonObject, "addition"));
            ItemStack itemStack = outputFromJson(JsonHelper.getObject(jsonObject, "result"));
            return new FletchingRecipe(identifier, bottomIngredient, middleIngredient, topIngredient, additionItemStack, itemStack);
        }

        @Override
        public FletchingRecipe read(Identifier identifier, PacketByteBuf packetByteBuf) {
            Ingredient topIngredient = Ingredient.fromPacket(packetByteBuf);
            Ingredient middleIngredient = Ingredient.fromPacket(packetByteBuf);
            Ingredient bottomIngredient = Ingredient.fromPacket(packetByteBuf);
            ItemStack additionItemStack = packetByteBuf.readItemStack();
            ItemStack itemStack = packetByteBuf.readItemStack();
            return new FletchingRecipe(identifier, bottomIngredient, middleIngredient, topIngredient, additionItemStack, itemStack);
        }

        @Override
        public void write(PacketByteBuf packetByteBuf, FletchingRecipe fletchingRecipe) {
            fletchingRecipe.top.write(packetByteBuf);
            fletchingRecipe.middle.write(packetByteBuf);
            fletchingRecipe.bottom.write(packetByteBuf);
            packetByteBuf.writeItemStack(fletchingRecipe.addition);
            packetByteBuf.writeItemStack(fletchingRecipe.result);
        }

    }
}
