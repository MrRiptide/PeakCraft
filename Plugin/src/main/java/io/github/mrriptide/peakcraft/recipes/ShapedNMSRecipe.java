package io.github.mrriptide.peakcraft.recipes;

import io.github.mrriptide.peakcraft.PeakCraft;
import net.minecraft.server.v1_16_R3.*;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftNamespacedKey;

import java.util.Arrays;
import java.util.HashMap;

public class ShapedNMSRecipe extends NMSRecipe {
    private HashMap<Character, RecipeItem> ingredientMap;
    private String[] shape;

    public ShapedNMSRecipe(ShapedRecipe sourceRecipe){
        this.setResult(sourceRecipe.getResult());
        this.setGroup(sourceRecipe.getGroup());
        this.shape = sourceRecipe.getShape();
        this.setKey(CraftNamespacedKey.toMinecraft(sourceRecipe.getKey()));

        this.ingredientMap = sourceRecipe.getIngredientMap();
    }

    /**
     *  Returns a boolean based on if the generic recipe matches the specific recipe passed in
     *
     * @TODO: OreDict feature for things like logs or stone
     *
     * @param   recipe  the crafted recipe to compare to
     * @return          if the generic recipe matches the specific recipe
     * */
    @Override
    public boolean test(NMSRecipe recipe){
        ShapedNMSRecipe shapedRecipe = (ShapedNMSRecipe)recipe;

        if (this == recipe || this.equals(recipe)){
            return true;
        }

        if (Arrays.equals(this.shape, shapedRecipe.shape)){
            if (this.ingredientMap.equals(shapedRecipe.ingredientMap)){
                return true;
            } else {
                for (Character key : this.ingredientMap.keySet()){
                    RecipeItem recipeItem = this.ingredientMap.get(key);
                    RecipeItem craftItem = shapedRecipe.ingredientMap.get(key);
                    if (!(craftItem.getCount() >= recipeItem.getCount() && (craftItem.getId().equals(recipeItem.getId())
                            || (!recipeItem.getOreDict().equals("") && recipeItem.getOreDict().equals(craftItem.getOreDict()))))){
                        return false;
                    }
                }
                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    // THEORY: TESTS TO SEE IF RECIPE MATCHES
    public boolean a(InventoryCrafting inventoryCrafting, World world) {
        PeakCraft.getPlugin().getLogger().info("Called a #1");
        throw new NotImplementedException("a1");
    }

    @Override
    /*
        Gets an instance of the result itemstack, no clue what purpose it holds though
     */
    public ItemStack a(InventoryCrafting inventoryCrafting) {
        PeakCraft.getPlugin().getLogger().info("Called a #2");
        return this.getResult().cloneItemStack();
    }

    @Override
    public RecipeSerializer<?> getRecipeSerializer() {
        throw new NotImplementedException("getRecipeSerializer");
    }

    @Override
    public Recipes<?> g() {
        return Recipes.CRAFTING;
    }

    @Override
    public org.bukkit.inventory.Recipe toBukkitRecipe() {
        throw new NotImplementedException("toBukkitRecipe");

    }
}
