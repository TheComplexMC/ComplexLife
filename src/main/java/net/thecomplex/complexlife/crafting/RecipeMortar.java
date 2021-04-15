package net.thecomplex.complexlife.crafting;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.thecomplex.complexlife.crafting.serializer.RecipeSerializerManager;
import net.thecomplex.complexlife.inventory.OnlyInputInventory;

public class RecipeMortar implements IOneIngredientRecipe {
    public final ResourceLocation id;
    public final ItemStack result;
    public final Ingredient ingredient;

    public RecipeMortar(ResourceLocation id, Ingredient ingredient, ItemStack result) {
        this.id = id;
        this.result = result;
        this.ingredient = ingredient;
    }

    @Override
    public ItemStack assemble(OnlyInputInventory inventory) {
        ItemStack input = inventory.getItem(0);
        return new ItemStack(result.getItem(), result.getCount() * (int)Math.floor(input.getCount() / ingredient.getItems()[0].getCount()));
    }

    @Override
    public boolean canCraftInDimensions(int xDimension, int yDimension) {
        return false;
    }

    @Override
    public ItemStack getResultItem() {
        return result;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(OnlyInputInventory inventory) {
        if(inventory.isEmpty()) {
            return NonNullList.withSize(1, ingredient.getItems()[0]);
        }
        return NonNullList.create();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> result = NonNullList.create();
        result.add(1, ingredient);
        return result;
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(Items.OBSIDIAN);
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return RecipeSerializerManager.MORTAR_RECIPE_SERIALIZER;
    }

    @Override
    public IRecipeType<?> getType() {
        return RecipeTypeManager.MORTAR;
    }

    @Override
    public Ingredient getIngredient() {
        return ingredient;
    }
}
