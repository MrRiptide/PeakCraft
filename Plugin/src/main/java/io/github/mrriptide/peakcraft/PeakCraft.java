package io.github.mrriptide.peakcraft;

import io.github.mrriptide.peakcraft.commands.*;
import io.github.mrriptide.peakcraft.items.ItemManager;
import io.github.mrriptide.peakcraft.items.enchantments.EnchantmentManager;
import io.github.mrriptide.peakcraft.items.enchantments.EnchantmentSharpness;
import io.github.mrriptide.peakcraft.listeners.EntityEventListener;
import io.github.mrriptide.peakcraft.listeners.GUIEventListener;
import io.github.mrriptide.peakcraft.recipes.RecipeManager;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;
import org.bukkit.plugin.java.JavaPlugin;

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
        //getServer().getPluginManager().registerEvents(new CraftingListener(), this);

        // Register all commands
        this.getCommand("give").setExecutor(new CommandGive());
        this.getCommand("reloaditems").setExecutor(new CommandReloadItems());
        this.getCommand("enchant").setExecutor(new CommandEnchant());
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

            /*if (vanilla_recipe instanceof BlastingRecipe){
                BlastingRecipe new_recipe = new BlastingRecipe(key,
                        (new RecipeItem(vanilla_recipe.getResult())).getItemStack(),
                        new RecipeItemChoice((new RecipeItem(((BlastingRecipe) vanilla_recipe).getInput()))),
                        ((BlastingRecipe) vanilla_recipe).getExperience(),
                        ((BlastingRecipe) vanilla_recipe).getCookingTime());
                Bukkit.addRecipe(new_recipe);
            } else if (vanilla_recipe instanceof CampfireRecipe){
                CampfireRecipe new_recipe = new CampfireRecipe(key,
                        (new RecipeItem(vanilla_recipe.getResult())).getItemStack(),
                        new RecipeItemChoice((new RecipeItem(((CampfireRecipe) vanilla_recipe).getInput()))),
                        ((CampfireRecipe) vanilla_recipe).getExperience(),
                        ((CampfireRecipe) vanilla_recipe).getCookingTime());
                Bukkit.addRecipe(new_recipe);
            } else if (vanilla_recipe instanceof SmokingRecipe) {
                SmokingRecipe new_recipe = new SmokingRecipe(key,
                        (new RecipeItem(vanilla_recipe.getResult())).getItemStack(),
                        new RecipeItemChoice((new RecipeItem(((SmokingRecipe) vanilla_recipe).getInput()))),
                        ((SmokingRecipe) vanilla_recipe).getExperience(),
                        ((SmokingRecipe) vanilla_recipe).getCookingTime());
                Bukkit.addRecipe(new_recipe);
            } else if (vanilla_recipe instanceof FurnaceRecipe) {
                FurnaceRecipe new_recipe = new FurnaceRecipe(key,
                        (new RecipeItem(vanilla_recipe.getResult())).getItemStack(),
                        new RecipeItemChoice((new RecipeItem(((FurnaceRecipe) vanilla_recipe).getInput()))),
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
            } else */if (vanilla_recipe instanceof ShapedRecipe) {
                io.github.mrriptide.peakcraft.recipes.ShapedRecipe new_recipe = new io.github.mrriptide.peakcraft.recipes.ShapedRecipe((ShapedRecipe) vanilla_recipe);

                // Register it directly through nms using RecipeManager
                RecipeManager.registerRecipe(((Keyed)vanilla_recipe).getKey().getKey(), new_recipe);
            }/* else if (vanilla_recipe instanceof ShapelessRecipe) {
                ShapelessRecipe new_recipe = new ShapelessRecipe(key,
                        (new RecipeItem(vanilla_recipe.getResult())).getItemStack());
                new_recipe.setGroup(((ShapelessRecipe) vanilla_recipe).getGroup());

                ArrayList<RecipeItem> ingredients = new ArrayList<>();
                for (ItemStack ingredient : ((ShapelessRecipe) vanilla_recipe).getIngredientList()){
                    ingredients.add(new RecipeItem(ingredient));
                }

                for (RecipeItem ingredient : ingredients){
                    new_recipe.addIngredient(new RecipeItemChoice(ingredient));
                }

                // Register it in the spigot recipe system
                Bukkit.addRecipe(new_recipe);
                // Register it in the internal recipe system
                RecipeManager.registerRecipe(((Keyed)vanilla_recipe).getKey().getKey(), new io.github.mrriptide.peakcraft.recipes.ShapelessRecipe(ingredients, new RecipeItem(vanilla_recipe.getResult())));
            }/* else if (vanilla_recipe instanceof SmithingRecipe) {
                SmithingRecipe new_recipe = new SmithingRecipe(key,
                        (new RecipeItem(vanilla_recipe.getResult())).getItemStack(),
                        new RecipeItemChoice((new RecipeItem(((SmithingRecipe) vanilla_recipe).getBase().getItemStack()))),
                        new RecipeItemChoice((new RecipeItem(((SmithingRecipe) vanilla_recipe).getAddition().getItemStack()))));
                Bukkit.addRecipe(new_recipe);
            } else if (vanilla_recipe instanceof StonecuttingRecipe){
                StonecuttingRecipe new_recipe = new StonecuttingRecipe(key,
                        (new RecipeItem(vanilla_recipe.getResult())).getItemStack(),
                        new RecipeItemChoice((new RecipeItem(((StonecuttingRecipe) vanilla_recipe).getInput()))));
                Bukkit.addRecipe(new_recipe);
            }*/
        }

        // Register all recipes

        getLogger().info("Registering recipes:");
        RecipeManager.registerRecipes();

        // Register enchants

        getLogger().info("Registering enchants:");
        EnchantmentManager.registerEnchantment(new EnchantmentSharpness());
    }
    @Override
    public void onDisable() {
        getLogger().info("onDisable is called!");
    }

    public static void disable(){
        getPlugin().getServer().getPluginManager().disablePlugin(instance);
    }
}
