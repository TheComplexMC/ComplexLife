package net.thecomplex.complexlife.crafting;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.world.World;
import net.thecomplex.complexlife.inventory.OnlyInputInventory;

public interface IOneIngredientRecipe extends IRecipe<OnlyInputInventory> {
    public Ingredient getIngredient();

    default @Override
    public boolean matches(OnlyInputInventory inventory, World world) {
        ItemStack input = inventory.getItem(0);
        for(int i = 0; i < getIngredient().getItems().length; i++) {
            if(input.getItem() == getIngredient().getItems()[i].getItem() && input.getCount() >= getIngredient().getItems()[i].getCount())
                return true;
        }
        return false;
    }
}
