package io.github.mrriptide.peakcraft.recipes;

import io.github.mrriptide.peakcraft.PeakCraft;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ShapedRecipe extends Recipe {
    private HashMap<Character, RecipeItem> ingredientMap;
    private String[] shape;

    public ShapedRecipe(){
        ingredientMap = new HashMap<>();
        this.setResult(null);
        this.setGroup("");
    }

    public ShapedRecipe(org.bukkit.inventory.ShapedRecipe recipeSource){
        this.setResult(new RecipeItem(recipeSource.getResult()));
        this.setGroup(recipeSource.getGroup());
        this.shape = recipeSource.getShape();

        this.ingredientMap = new HashMap<>();
        if (shape != null){
            for (Character key : recipeSource.getIngredientMap().keySet()){
                if (recipeSource.getIngredientMap().get(key) != null){
                    ingredientMap.put(key, new RecipeItem(recipeSource.getIngredientMap().get(key)));
                } else {
                    for (int i = 0; i < shape.length; i++){
                        shape[i] = shape[i].replace(key, ' ');
                    }
                }
            }
        }
    }

    public ShapedRecipe(RecipeItem[][] ingredients, RecipeItem result){
        this.setResult(result);
        this.setGroup("");

        ArrayList<Integer> valid_cols = new ArrayList<>();
        ArrayList<Integer> valid_rows = new ArrayList<>();

        // loop through two dimensions
        for (int i = 0; i <= 2; i++){
            boolean row_contains_item = false;
            boolean col_contains_item = false;
            for (int j = 0; j <= 2; j++){
                // checks if column is empty
                if (ingredients[j][i].getItemStack().getType() != Material.AIR){
                    col_contains_item = true;
                }
                // checks if row is empty
                if (ingredients[i][j].getItemStack().getType() != Material.AIR){
                    row_contains_item = true;
                }
            }
            if (row_contains_item){
                valid_rows.add(i);
            }
            if (col_contains_item){
                valid_cols.add(i);
            }
        }

        // build the shape
        shape = new String[valid_rows.size()];

        // hashmap of the ingredients
        HashMap<RecipeItem, Character> reversedIngredientMap = new HashMap<>();
        Character[] ingredient_chars = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i'};
        int i = 0;
        // go through only the valid rows and columns
        for (int row : valid_rows){
            shape[i] = "";
            for (int col : valid_cols){
                RecipeItem ingredient = ingredients[row][col];
                // if it is air, leave it blank in the recipe
                if (ingredient.getItemStack().getType() == Material.AIR){
                    shape[i] += " ";
                    continue;
                }
                // if it isnt already a defined ingredient, define it in the hashmap
                if (!reversedIngredientMap.containsKey(ingredient)) {
                    reversedIngredientMap.put(ingredient, ingredient_chars[reversedIngredientMap.size()]);
                }

                // use the hashmap to use consistent keys
                shape[i] += reversedIngredientMap.get(ingredient);
            }
            i++;
        }

        ingredientMap = new HashMap<>();
        for (RecipeItem ingredient : reversedIngredientMap.keySet()){
            ingredientMap.put(reversedIngredientMap.get(ingredient), ingredient);
        }
    }

    public HashMap<Character, RecipeItem> getIngredients(){
        return ingredientMap;
    }

    public String[] getShape(){return shape;}

    public HashMap<Character, RecipeItem> getIngredientMap(){
        return ingredientMap;
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
    public boolean test(Recipe recipe){
        ShapedRecipe shapedRecipe = (ShapedRecipe)recipe;

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

        /*// if they dont have the same number of rows
        if (this.shape.length != shapedRecipe.shape.length){
            return false;
        }

        // if they dont have the same number of columns
        if (this.shape[0].length() != shapedRecipe.shape[0].length()){
            return false;
        }


        PeakCraft.getPlugin().getLogger().info("THIS SHOULD NOT BE SEEN");
        for (int row = 0; row < this.shape.length; row++){
            for (int col = 0; col < this.shape[row].length(); col++){
                char recipeChar = this.shape[row].charAt(col);
                char craftChar = shapedRecipe.shape[row].charAt(col);
                // if the character in both shapes are spaces then skip
                if (' ' == recipeChar && ' ' == craftChar){
                    continue;
                }

                // if the character in only one shape is a space then return false
                if (' ' == recipeChar || ' ' == craftChar){
                    return false;
                }

                RecipeItem recipeItem = this.ingredientMap.get(this.shape[row].charAt(col));
                RecipeItem craftItem = shapedRecipe.ingredientMap.get(shapedRecipe.shape[row].charAt(col));
                // if ids are different
                if (!recipeItem.getId().equals(craftItem.getId())){
                    // if there is no oredict
                    if (recipeItem.getOreDict().equals("")){
                        return false;
                    } else if (!recipeItem.getOreDict().equals(craftItem.getOreDict())){
                        // if there is an oredict but it doesnt match
                        return false;
                    }
                }
            }
        }

        // if it doesnt find any reason anywhere else to
        return true;*/
    }

}
