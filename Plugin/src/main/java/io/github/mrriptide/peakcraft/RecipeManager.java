package io.github.mrriptide.peakcraft;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class RecipeManager {
    //private static HashMap<String, Recipe> recipes = new HashMap<>();

    public static Recipe getRecipe(String name) {
        File recipeFile = new File(PeakCraft.instance.getDataFolder() + File.separator + "recipes" + File.separator + name.toLowerCase() + ".json");
        if (recipeFile.exists()){
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(recipeFile, Recipe.class);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        } else{
            return null;
        }
    }

    public static boolean recipeExists(String name){
        File recipeFile = new File(PeakCraft.instance.getDataFolder() + File.separator + "recipes" + File.separator + name.toLowerCase() + ".json");
        return recipeFile.exists();
    }

    public static void saveRecipe(Recipe recipe, String recipeName){
        // check to make sure that the file can exist(make the recipe folder if it doesnt exist)
        File recipeFolder = new File(PeakCraft.instance.getDataFolder() + File.separator + "recipes");

        if (!recipeFolder.exists()){
            recipeFolder.mkdirs();
        }

        // save using jackson https://stackabuse.com/reading-and-writing-json-in-java/

        File recipeFile = new File(recipeFolder + File.separator + recipeName.toLowerCase() + ".json");
        try {
            OutputStream outputStream = new FileOutputStream(recipeFile);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(outputStream, recipe);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void registerRecipe(String recipe_name){
        recipe_name = recipe_name.toLowerCase();
        if (recipeExists(recipe_name)){
            Recipe recipe_data = getRecipe(recipe_name);
            NamespacedKey key = new NamespacedKey(PeakCraft.instance, recipe_name);
            if (Bukkit.getRecipe(key) != null){
                PeakCraft.getPlugin().getLogger().info("Overriding existing recipe \"" + recipe_name + "\"");
                Bukkit.removeRecipe(key);
            }
            ShapedRecipe recipe = new ShapedRecipe(key, recipe_data.getResult().getItemStack());

            RecipeItem[][] ingredients = recipe_data.getIngredients();

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
            String[] shape = new String[valid_rows.size()];

            // hashmap of the ingredients
            HashMap<RecipeItem, Character> ingredients_dict = new HashMap<>();
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
                    if (!ingredients_dict.containsKey(ingredient)) {
                        ingredients_dict.put(ingredient, ingredient_chars[ingredients_dict.size()]);
                    }

                    // use the hashmap to use consistent keys
                    shape[i] += ingredients_dict.get(ingredient);
                }
                i++;
            }


            /*PeakCraft.getPlugin().getLogger().info("Recipe shape:\n" + String.join("\n", shape));
            PeakCraft.getPlugin().getLogger().info(valid_rows.toString());
            PeakCraft.getPlugin().getLogger().info(valid_cols.toString());
            PeakCraft.getPlugin().getLogger().info(ingredients_dict.toString());*/
            // assign the shape
            recipe.shape(shape);

            // add each ingredient's keys
            for (RecipeItem ingredient : ingredients_dict.keySet()){
                recipe.setIngredient(ingredients_dict.get(ingredient), new RecipeChoice.ExactChoice(ingredient.getItemStack()));
            }

            Bukkit.addRecipe(recipe);
        }
    }

    public static void registerRecipes(){
        File recipeFolder = new File(PeakCraft.instance.getDataFolder() + File.separator + "recipes");

        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".json");
            }
        };

        String[] recipe_names = recipeFolder.list(filter);
        assert recipe_names != null;

        for (String recipe_name : recipe_names){
            registerRecipe(recipe_name.replace(".json", ""));
            PeakCraft.getPlugin().getLogger().info("\tRegistered \"" + recipe_name.replace(".json", "") + "\"");
        }
    }
}
