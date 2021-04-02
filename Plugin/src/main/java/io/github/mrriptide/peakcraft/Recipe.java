package io.github.mrriptide.peakcraft;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ShapedRecipe;

import java.util.ArrayList;
import java.util.HashMap;

public class Recipe {
    private HashMap<RecipeItem, Character> ingredientMap;
    private String[] shape;
    private RecipeItem result;
    private String group;

    public Recipe(){
        ingredientMap = new HashMap<>();
        result = null;
        group = "";
    }

    public Recipe(RecipeItem[][] ingredients, RecipeItem result){
        this.result = result;
        this.group = "";

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
        ingredientMap = new HashMap<>();
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
                if (!ingredientMap.containsKey(ingredient)) {
                    ingredientMap.put(ingredient, ingredient_chars[ingredientMap.size()]);
                }

                // use the hashmap to use consistent keys
                shape[i] += ingredientMap.get(ingredient);
            }
            i++;
        }
    }

    public void setIngredientMap(HashMap<RecipeItem, Character> ingredientMap){
        this.ingredientMap = ingredientMap;
    }

    public void setResult(RecipeItem item){
        this.result = item;
    }

    public HashMap<RecipeItem, Character> getIngredients(){
        return ingredientMap;
    }

    public RecipeItem getResult(){
        return result;
    }

    public void setGroup(String group){
        this.group = group;
    }

    public String getGroup(){
        return group;
    }

}
