package io.github.mrriptide.peakcraft.recipes;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;

public class ShapelessRecipe extends Recipe{
    private ArrayList<RecipeItem> ingredients;

    public ShapelessRecipe(ArrayList<RecipeItem> ingredients, RecipeItem result){
        this.ingredients = ingredients;
        this.setResult(result);
    }

    public ShapelessRecipe(org.bukkit.inventory.ShapelessRecipe recipeSource){
        this.setResult(new RecipeItem(recipeSource.getResult()));
        this.setGroup(recipeSource.getGroup());

        this.ingredients = new ArrayList<>();
        for (ItemStack ingredient : recipeSource.getIngredientList()){
            this.ingredients.add(new RecipeItem(ingredient));
        }
    }

    public ArrayList<RecipeItem> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<RecipeItem> ingredients){
        this.ingredients = ingredients;
    }

    /**
     *  Returns a boolean based on if the generic recipe matches the specific recipe passed in
     *
     * @TODO: OreDict feature for things like logs or stone
     *
     * @param   ingredients_arr  the ingredients to compare to
     * @return          if the generic recipe matches the specific recipe
     * */
    @Override
    public boolean test(RecipeItem[][] ingredients_arr) {
        ArrayList<RecipeItem> itemList = new ArrayList<>();

        for (RecipeItem[] row : ingredients_arr){
            itemList.addAll(Arrays.asList(row));
        }

        // loop through all ingredients in the recipe
        for (RecipeItem ingredient : ingredients){
            boolean itemFound = false;
            // loop through all items in the crafting matrix
            for (RecipeItem item : itemList){
                // if the item has the same id and has at least the count required
                if (item.getId().equals(ingredient.getId()) && item.getCount() >= ingredient.getCount()){
                    itemFound = true;
                    itemList.remove(item);
                    break;
                }
            }
            // if the ingredient is not found, return false
            if (!itemFound){
                return false;
            }
        }

        if (itemList.size() == 0){
            // all the items in the matrix are used, meaning it is valid
            return true;
        } else {
            // there are invalid items in the matrix
            return false;
        }
    }
}
