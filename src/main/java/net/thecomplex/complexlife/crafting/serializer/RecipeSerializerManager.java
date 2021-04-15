package net.thecomplex.complexlife.crafting.serializer;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.thecomplex.complexlife.crafting.RecipeMortar;

public class RecipeSerializerManager {
    private static RecipeSerializerManager _instance;

    public static RecipeSerializerManager getInstance() {
        if(_instance == null)
            _instance = new RecipeSerializerManager();
        return _instance;
    }

    public static IRecipeSerializer<RecipeMortar> MORTAR_RECIPE_SERIALIZER = new OneIngredientRecipeSerializer<RecipeMortar>(RecipeMortar::new).setRegistryName(new ResourceLocation("complexlife", "mortar"));

    public void RegisterRecipeSerializer(IForgeRegistry<IRecipeSerializer<?>> register) {
        register.register(MORTAR_RECIPE_SERIALIZER);
    }
}
