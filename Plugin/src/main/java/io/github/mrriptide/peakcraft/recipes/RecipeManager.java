package io.github.mrriptide.peakcraft.recipes;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.mrriptide.peakcraft.PeakCraft;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class RecipeManager {
    private static HashMap<String, Recipe> recipes = new HashMap<>();

    /** Gets a recipe from the internal cache of recipes
     *
     * @param name  name of the recipe to get
     * @return      instance of the recipe gotten from the internal cache
     */
    public static Recipe getRecipe(String name){
        return recipes.get(name);
    }

    /** Loads a recipe from the file system
     *
     * @param name  name of the file system to load
     * @return      instance of the recipe gotten from the file system
     */
    public static ShapedRecipe loadRecipe(String name) {
        File recipeFile = new File(PeakCraft.instance.getDataFolder() + File.separator + "recipes" + File.separator + name.toLowerCase() + ".json");
        if (recipeFile.exists()){
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(recipeFile, ShapedRecipe.class);
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

    public static void saveRecipe(ShapedRecipe recipe, String recipeName){
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

    public static void registerRecipe(String recipeName){
        recipeName = recipeName.toLowerCase();
        if (recipeExists(recipeName)){
            Recipe recipe = loadRecipe(recipeName.replace(".json", ""));

            registerRecipe(recipeName, recipe);
        }
    }

    public static void registerRecipe(String recipeName, Recipe recipe) {
        recipes.put(recipeName, recipe);

        // register it directly to the server
        ((CraftServer) Bukkit.getServer()).getServer().getCraftingManager().addRecipe(recipe.toNMS(recipeName));
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

    public static Recipe getMatch(ItemStack[] raw_ingredients){
        //long startTime = System.nanoTime();
        RecipeItem[][] ingredients = new RecipeItem[3][3];
        assert raw_ingredients != null;
        for (int i = 0; i < 3; i++){
            for (int j = 0; j < 3; j++){
                ItemStack itemStack = raw_ingredients[i * 3 + j];
                if (itemStack != null) {
                    ingredients[i][j] = new RecipeItem(itemStack);
                } else {
                    ingredients[i][j] = new RecipeItem("AIR");
                }
            }
        }
        /*long endTime = System.nanoTime();
        long duration = (endTime-startTime) / 1000000;
        PeakCraft.getPlugin().getLogger().info("It took " + Math.round(duration * 100) / 100.0 + "ms to generate the ingredient array");*/

        ShapedRecipe craftedShapedRecipe = new ShapedRecipe(ingredients, new RecipeItem("air"));
        ShapelessRecipe craftedShapelessRecipe = new ShapelessRecipe(ingredients, new RecipeItem("air"));

        /*long shapedTime = 0;
        int shapedTested = 0;
        long shapelessTime = 0;
        int shapelessTested = 0;
        startTime = System.nanoTime();
        int recipesTested = 0;*/
        for (Recipe recipe : recipes.values()){
            //recipesTested++;
            // only check for shaped or shapeless recipes
            if (!(recipe instanceof ShapedRecipe || recipe instanceof ShapelessRecipe)){
                continue;
            }
            boolean recipeMatch;
            //long start = System.nanoTime();
            if (recipe instanceof ShapedRecipe){
                recipeMatch = recipe.test(craftedShapedRecipe);
                //long end = System.nanoTime();
               // shapedTime += (end-start);
              //  shapedTested++;
            } else {
                recipeMatch = recipe.test(craftedShapelessRecipe);
                //long end = System.nanoTime();
              //  shapelessTime += (end-start);
             //   shapelessTested++;
            }
            if (recipeMatch){
                return recipe;
                /*endTime = System.nanoTime();
                duration = (endTime-startTime) / 1000000;

                PeakCraft.getPlugin().getLogger().info("It took " + Math.round(duration * 100) / 100.0 + "ms to test " + recipesTested + " recipes," +
                        " an average of " + Math.round(duration * 100.0 / recipesTested) / 100.0 + "ms per recipe");
                PeakCraft.getPlugin().getLogger().info("The shaped recipes took " + Math.round(shapedTime / 1000000.0 * 100) / 100.0 + "ms to test " + shapedTested +
                        " recipes, an average of " + Math.round(shapedTime / 1000000.0 * 100.0 / shapedTested) / 100.0 + "ms per recipe");
                PeakCraft.getPlugin().getLogger().info("The shapeless recipes took " + Math.round(shapelessTime / 1000000.0 * 100) / 100.0 + "ms to test " + shapelessTested +
                        " recipes, an average of " + Math.round(shapelessTime / 1000000.0 * 100.0 / shapelessTested) / 100.0 + "ms per recipe");*/
            }
        }
        /*endTime = System.nanoTime();
        duration = (endTime-startTime) / 1000000;
        PeakCraft.getPlugin().getLogger().info("It took " + Math.round(duration * 100) / 100.0 + "ms to test " + recipesTested + " recipes," +
                " an average of " + Math.round(duration * 100.0 / recipesTested) / 100.0 + "ms per recipe");
        PeakCraft.getPlugin().getLogger().info("The shaped recipes took " + Math.round(shapedTime / 1000000.0 * 100) / 100.0 + "ms to test " + shapedTested +
                " recipes, an average of " + Math.round(shapedTime / 1000000.0 * 100.0 / shapedTested) / 100.0 + "ms per recipe");
        PeakCraft.getPlugin().getLogger().info("The shapeless recipes took " + Math.round(shapelessTime / 1000000.0 * 100) / 100.0 + "ms to test " + shapelessTested +
                " recipes, an average of " + Math.round(shapelessTime / 1000000.0 * 100.0 / shapelessTested) / 100.0 + "ms per recipe");*/

        return null;
    }

    public static RecipeItem getResult(ItemStack[] raw_ingredients){
        Recipe match = getMatch(raw_ingredients);
        if (match == null){
            return null;
        } else {
            return match.getResult();
        }
    }
}
