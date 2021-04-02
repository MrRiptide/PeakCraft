package io.github.mrriptide.peakcraft;

import io.github.mrriptide.peakcraft.commands.CommandRecipe;
import io.github.mrriptide.peakcraft.commands.CommandGive;
import io.github.mrriptide.peakcraft.commands.CommandRecipe_Add;
import io.github.mrriptide.peakcraft.commands.CommandReloadItems;
import io.github.mrriptide.peakcraft.listeners.CraftingListener;
import io.github.mrriptide.peakcraft.listeners.EntityEventListener;
import io.github.mrriptide.peakcraft.listeners.GUIEventListener;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Iterator;

public class PeakCraft extends JavaPlugin {
    public static PeakCraft instance;

    public static PeakCraft getPlugin(){
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this; // provides an instance of the plugin to the rest of the code !!MUST BE DONE BEFORE ANYTHING ELSE!!

        // Load items from items.tsv, must be done as one of the first things
        getLogger().info("Loading items");
        ItemManager.loadItems();

        // Register event listeners

        getServer().getPluginManager().registerEvents(new EntityEventListener(), this);
        getServer().getPluginManager().registerEvents(new GUIEventListener(), this);
        getServer().getPluginManager().registerEvents(new CraftingListener(), this);

        // Register all commands
        this.getCommand("give").setExecutor(new CommandGive());
        this.getCommand("reloaditems").setExecutor(new CommandReloadItems());
        // Register recipe commands
        CommandRecipe commandRecipe = new CommandRecipe();
        this.getCommand("recipe").setExecutor(commandRecipe);
        commandRecipe.registerCommand("add", new CommandRecipe_Add());

        // Get an iterator for all the recipes and save it to be iterated through later
        Iterator<org.bukkit.inventory.Recipe> recipeIterator = Bukkit.recipeIterator();

        // Clear all vanilla recipes
        Bukkit.clearRecipes();

        // Re-Register Vanilla Recipes
        while (recipeIterator.hasNext()) {
            org.bukkit.inventory.Recipe vanilla_recipe = (org.bukkit.inventory.Recipe) recipeIterator.next();
            String recipe_name = ((Keyed)vanilla_recipe).getKey().getKey();

            if (RecipeManager.recipeExists(recipe_name)){
                getLogger().info("Not re-defining vanilla recipe for " + recipe_name + " because it has a custom-defined recipe with the same name.");
                return;
            } else {
                getLogger().info("Re-defining vanilla recipe \"" + recipe_name + "\" (" + vanilla_recipe.getClass().getName().split("\\.")[vanilla_recipe.getClass().getName().split("\\.").length-1] + ")");
            }

            // Create the namespaced key
            NamespacedKey key = new NamespacedKey(this, recipe_name);

            if (vanilla_recipe instanceof BlastingRecipe){
                BlastingRecipe new_recipe = new BlastingRecipe(key,
                        (new RecipeItem(vanilla_recipe.getResult())).getItemStack(),
                        new RecipeChoice.ExactChoice((new RecipeItem(((BlastingRecipe) vanilla_recipe).getInput())).getItemStack()),
                        ((BlastingRecipe) vanilla_recipe).getExperience(),
                        ((BlastingRecipe) vanilla_recipe).getCookingTime());
                Bukkit.addRecipe(new_recipe);
            } else if (vanilla_recipe instanceof CampfireRecipe){
                CampfireRecipe new_recipe = new CampfireRecipe(key,
                        (new RecipeItem(vanilla_recipe.getResult())).getItemStack(),
                        new RecipeChoice.ExactChoice((new RecipeItem(((CampfireRecipe) vanilla_recipe).getInput())).getItemStack()),
                        ((CampfireRecipe) vanilla_recipe).getExperience(),
                        ((CampfireRecipe) vanilla_recipe).getCookingTime());
                Bukkit.addRecipe(new_recipe);
            } else if (vanilla_recipe instanceof SmokingRecipe) {
                SmokingRecipe new_recipe = new SmokingRecipe(key,
                        (new RecipeItem(vanilla_recipe.getResult())).getItemStack(),
                        new RecipeChoice.ExactChoice((new RecipeItem(((SmokingRecipe) vanilla_recipe).getInput())).getItemStack()),
                        ((SmokingRecipe) vanilla_recipe).getExperience(),
                        ((SmokingRecipe) vanilla_recipe).getCookingTime());
                Bukkit.addRecipe(new_recipe);
            } else if (vanilla_recipe instanceof FurnaceRecipe) {
                FurnaceRecipe new_recipe = new FurnaceRecipe(key,
                        (new RecipeItem(vanilla_recipe.getResult())).getItemStack(),
                        new RecipeChoice.ExactChoice((new RecipeItem(((FurnaceRecipe) vanilla_recipe).getInput())).getItemStack()),
                        ((FurnaceRecipe) vanilla_recipe).getExperience(),
                        ((FurnaceRecipe) vanilla_recipe).getCookingTime());
                Bukkit.addRecipe(new_recipe);
            } else if (vanilla_recipe instanceof MerchantRecipe) {
                MerchantRecipe new_recipe = new MerchantRecipe((new RecipeItem(vanilla_recipe.getResult())).getItemStack(),
                        ((MerchantRecipe) vanilla_recipe).getUses(), ((MerchantRecipe) vanilla_recipe).getMaxUses(),
                        ((MerchantRecipe) vanilla_recipe).hasExperienceReward(),
                        ((MerchantRecipe) vanilla_recipe).getVillagerExperience(),
                        ((MerchantRecipe) vanilla_recipe).getPriceMultiplier());
                for (ItemStack ingredient : ((MerchantRecipe) vanilla_recipe).getIngredients()){
                    new_recipe.addIngredient((new RecipeItem(ingredient)).getItemStack());
                }
                Bukkit.addRecipe(new_recipe);
            } else if (vanilla_recipe instanceof ShapedRecipe) {
                ShapedRecipe new_recipe = new ShapedRecipe(key,
                        (new RecipeItem(vanilla_recipe.getResult())).getItemStack());
                new_recipe.setGroup(((ShapedRecipe) vanilla_recipe).getGroup());
                String[] shape = ((ShapedRecipe) vanilla_recipe).getShape();
                new_recipe.shape(shape);

                for (Character ingredient_key : ((ShapedRecipe) vanilla_recipe).getIngredientMap().keySet()){
                    if (((ShapedRecipe) vanilla_recipe).getIngredientMap().get(ingredient_key) == null){
                        for (int i = 0; i < shape.length; i++){
                            shape[i] = shape[i].replace(ingredient_key, ' ');
                        }
                    } else {
                        new_recipe.setIngredient(ingredient_key,
                                new RecipeChoice.ExactChoice((new RecipeItem(((ShapedRecipe) vanilla_recipe).getIngredientMap().get(ingredient_key))).getItemStack()));
                    }
                }

                new_recipe.shape(shape);

                Bukkit.addRecipe(new_recipe);
            } else if (vanilla_recipe instanceof ShapelessRecipe) {
                ShapelessRecipe new_recipe = new ShapelessRecipe(key,
                        (new RecipeItem(vanilla_recipe.getResult())).getItemStack());
                new_recipe.setGroup(((ShapelessRecipe) vanilla_recipe).getGroup());

                for (ItemStack ingredient : ((ShapelessRecipe) vanilla_recipe).getIngredientList()){
                    new_recipe.addIngredient(new RecipeChoice.ExactChoice((new RecipeItem(ingredient)).getItemStack()));
                }

                Bukkit.addRecipe(new_recipe);
            } else if (vanilla_recipe instanceof SmithingRecipe) {
                SmithingRecipe new_recipe = new SmithingRecipe(key,
                        (new RecipeItem(vanilla_recipe.getResult())).getItemStack(),
                        new RecipeChoice.ExactChoice((new RecipeItem(((SmithingRecipe) vanilla_recipe).getBase().getItemStack())).getItemStack()),
                        new RecipeChoice.ExactChoice((new RecipeItem(((SmithingRecipe) vanilla_recipe).getAddition().getItemStack())).getItemStack()));
                Bukkit.addRecipe(new_recipe);
            } else if (vanilla_recipe instanceof StonecuttingRecipe){
                StonecuttingRecipe new_recipe = new StonecuttingRecipe(key,
                        (new RecipeItem(vanilla_recipe.getResult())).getItemStack(),
                        new RecipeChoice.ExactChoice((new RecipeItem(((StonecuttingRecipe) vanilla_recipe).getInput())).getItemStack()));
                Bukkit.addRecipe(new_recipe);
            }
        }

        // Register all recipes

        getLogger().info("Registering recipes:");
        RecipeManager.registerRecipes();
    }
    @Override
    public void onDisable() {
        getLogger().info("onDisable is called!");
    }

    public static void disable(){
        getPlugin().getServer().getPluginManager().disablePlugin(instance);
    }
}
