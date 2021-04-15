package net.thecomplex.complexlife.crafting.serializer;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.thecomplex.complexlife.crafting.IOneIngredientRecipe;
import net.thecomplex.complexlife.crafting.RecipeMortar;

import javax.annotation.Nullable;

public class OneIngredientRecipeSerializer<R extends IOneIngredientRecipe> implements IRecipeSerializer<R> {
    private ResourceLocation id;
    private final IFactory<?> factory;
    private final TypeToken<IRecipeSerializer<?>> token = new TypeToken<IRecipeSerializer<?>>(getClass()){};

    public OneIngredientRecipeSerializer(IFactory<?> factory) {
        this.factory = factory;
    }

    @Override
    public R fromJson(ResourceLocation id, JsonObject jsonObject) {
        JsonElement jsonelement = (JsonElement)(JSONUtils.isArrayNode(jsonObject, "ingredient") ? JSONUtils.getAsJsonArray(jsonObject, "ingredient") : JSONUtils.getAsJsonObject(jsonObject, "ingredient"));
        Ingredient ingredient = Ingredient.fromJson(jsonelement);
        //Forge: Check if primitive string to keep vanilla or a object which can contain a count field.
        if (!jsonObject.has("result")) throw new com.google.gson.JsonSyntaxException("Missing result, expected to find a string or object");
        ItemStack itemstack;
        if (jsonObject.get("result").isJsonObject()) itemstack = ShapedRecipe.itemFromJson(JSONUtils.getAsJsonObject(jsonObject, "result"));
        else {
            String s1 = JSONUtils.getAsString(jsonObject, "result");
            ResourceLocation resourcelocation = new ResourceLocation(s1);
            itemstack = new ItemStack(Registry.ITEM.getOptional(resourcelocation).orElseThrow(() -> {
                return new IllegalStateException("Item: " + s1 + " does not exist");
            }));
        }
        return (R)this.factory.create(id, ingredient, itemstack);
    }

    @Nullable
    @Override
    public R fromNetwork(ResourceLocation id, PacketBuffer buffer) {
        Ingredient ingredient = Ingredient.fromNetwork(buffer);
        ItemStack itemstack = buffer.readItem();
        return (R)this.factory.create(id, ingredient, itemstack);
    }

    @Override
    public void toNetwork(PacketBuffer buffer, R recipe) {
        recipe.getIngredient().toNetwork(buffer);
        buffer.writeItem(((RecipeMortar)recipe).result);
    }

    @Override
    public IRecipeSerializer<R> setRegistryName(ResourceLocation name) {
        id = name;
        return this;
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName() {
        return id;
    }

    @Override
    public Class<IRecipeSerializer<?>> getRegistryType() {
        return (Class<IRecipeSerializer<?>>) token.getRawType();
    }

    interface IFactory<R extends IRecipe<?>> {
        R create(ResourceLocation id, Ingredient ingredient, ItemStack result);
    }
}
