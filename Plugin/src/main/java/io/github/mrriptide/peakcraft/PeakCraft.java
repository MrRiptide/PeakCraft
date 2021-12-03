package io.github.mrriptide.peakcraft;

import io.github.mrriptide.peakcraft.commands.*;
import io.github.mrriptide.peakcraft.entity.EntityManager;
import io.github.mrriptide.peakcraft.items.ItemManager;
import io.github.mrriptide.peakcraft.items.abilities.AbilityManager;
import io.github.mrriptide.peakcraft.items.abilities.FlightFeatherAbility;
import io.github.mrriptide.peakcraft.items.abilities.InspectAbility;
import io.github.mrriptide.peakcraft.items.abilities.PotatoSwordAbility;
import io.github.mrriptide.peakcraft.items.enchantments.EnchantmentManager;
import io.github.mrriptide.peakcraft.items.fullsetbonus.FullSetBonusManager;
import io.github.mrriptide.peakcraft.items.fullsetbonus.SpaceSuitFullSetBonus;
import io.github.mrriptide.peakcraft.listeners.*;
import io.github.mrriptide.peakcraft.recipes.CustomItemStack;
import io.github.mrriptide.peakcraft.recipes.RecipeManager;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

public class PeakCraft extends JavaPlugin {
    public static PeakCraft instance;
    public static boolean generatingEntityDatabase = false;

    public static PeakCraft getPlugin(){
        return instance;
    }

    public static io.github.mrriptide.peakcraft.recipes.ShapedNMSRecipe find(ArrayList<io.github.mrriptide.peakcraft.recipes.ShapedNMSRecipe> collection){
        /*for (io.github.mrriptide.peakcraft.recipes.ShapedNMSRecipe recipe : collection){
            if (recipe.getId() == null){
                return recipe;
            }
        }*/
        return null;
    }

    @Override
    public void onEnable() {
        instance = this; // provides an instance of the plugin to the rest of the code !!MUST BE DONE BEFORE ANYTHING ELSE!!

        if(getServer().getPluginManager().getPlugin("Citizens") == null || !getServer().getPluginManager().getPlugin("Citizens").isEnabled()) {
            getLogger().log(Level.SEVERE, "Citizens 2.0 not found or not enabled");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Config stuff

        FileConfiguration config = this.getConfig();

        config.addDefault("itemBukkitVersion", "none");
        config.addDefault("pluginVersion", getDescription().getVersion());
        config.addDefault("database.host", "localhost");
        config.addDefault("database.port", 3306);
        config.addDefault("database.name", "db");
        config.addDefault("database.user", "username");
        config.addDefault("database.password", "password");

        config.options().copyDefaults(true);

        saveConfig();

        // Register abilities, must be done before loading items

        getLogger().info("Registering abilities:");
        AbilityManager.registerAbility(new PotatoSwordAbility());
        AbilityManager.registerAbility(new InspectAbility());
        AbilityManager.registerAbility(new FlightFeatherAbility());

        // Load items from items.tsv, must be done as one of the first things (especially before recipes are loaded)
        getLogger().info("Loading items");
        // checks for new items and saves them to the new items table
        ItemManager.loadNewItems();
        // loads the items from the database
        ItemManager.loadItems();

        getLogger().info("Registering entities");
        EntityManager.registerEntities();

        // Register event listeners

        //getServer().getPluginManager().registerEvents(new EntityEventListener(), this);
        //getServer().getPluginManager().registerEvents(new GUIEventListener(), this);
        //getServer().getPluginManager().registerEvents(new DamageListener(), this);
        //getServer().getPluginManager().registerEvents(new ChunkListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        // Register all commands
        this.getCommand("give").setExecutor(new CommandGive());
        this.getCommand("reloaditems").setExecutor(new CommandReloadItems());
        this.getCommand("enchant").setExecutor(new CommandEnchant());
        this.getCommand("summon").setExecutor(new CommandSummon());
        this.getCommand("heal").setExecutor(new CommandHeal());
        this.getCommand("kill").setExecutor(new CommandKill());
        this.getCommand("test").setExecutor(new CommandTest());
        this.getCommand("creativeinv").setExecutor(new CommandCreativeInventory());
        this.getCommand("materiallist").setExecutor(new CommandMaterialList());
        this.getCommand("vault").setExecutor(new CommandVault());
        this.getCommand("balance").setExecutor(new CommandBalance());
        this.getCommand("entitydatabase").setExecutor(new CommandEntityDatabase());
        this.getCommand("reloadentities").setExecutor(new CommandReloadEntities());
        // Register recipe commands
        CommandRecipe commandRecipe = new CommandRecipe();
        this.getCommand("recipe").setExecutor(commandRecipe);
        commandRecipe.registerCommand("add", new CommandRecipe_Add());

        /*// Get an iterator for all the recipes and save it to be iterated through later
        Iterator<org.bukkit.inventory.Recipe> recipeIterator = Bukkit.recipeIterator();

        // Clear all vanilla recipes
        Bukkit.clearRecipes();

        // Re-Register Vanilla Recipe
        while (recipeIterator.hasNext()) {
            org.bukkit.inventory.Recipe vanilla_recipe = recipeIterator.next();
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
                        (new CustomItemStack(vanilla_recipe.getResult())),
                        new RecipeChoice.ExactChoice((new CustomItemStack(((BlastingRecipe) vanilla_recipe).getInput()))),
                        ((BlastingRecipe) vanilla_recipe).getExperience(),
                        ((BlastingRecipe) vanilla_recipe).getCookingTime());
                Bukkit.addRecipe(new_recipe);
            } else if (vanilla_recipe instanceof CampfireRecipe){
                CampfireRecipe new_recipe = new CampfireRecipe(key,
                        (new CustomItemStack(vanilla_recipe.getResult())),
                        new RecipeChoice.ExactChoice((new CustomItemStack(((CampfireRecipe) vanilla_recipe).getInput()))),
                        ((CampfireRecipe) vanilla_recipe).getExperience(),
                        ((CampfireRecipe) vanilla_recipe).getCookingTime());
                Bukkit.addRecipe(new_recipe);
            } else if (vanilla_recipe instanceof SmokingRecipe) {
                SmokingRecipe new_recipe = new SmokingRecipe(key,
                        (new CustomItemStack(vanilla_recipe.getResult())),
                        new RecipeChoice.ExactChoice((new CustomItemStack(((SmokingRecipe) vanilla_recipe).getInput()))),
                        ((SmokingRecipe) vanilla_recipe).getExperience(),
                        ((SmokingRecipe) vanilla_recipe).getCookingTime());
                Bukkit.addRecipe(new_recipe);
            } else if (vanilla_recipe instanceof FurnaceRecipe) {
                FurnaceRecipe new_recipe = new FurnaceRecipe(key,
                        (new CustomItemStack(vanilla_recipe.getResult())),
                        new RecipeChoice.ExactChoice((new CustomItemStack(((FurnaceRecipe) vanilla_recipe).getInput()))),
                        ((FurnaceRecipe) vanilla_recipe).getExperience(),
                        ((FurnaceRecipe) vanilla_recipe).getCookingTime());
                Bukkit.addRecipe(new_recipe);
            } else if (vanilla_recipe instanceof MerchantRecipe) {
                MerchantRecipe new_recipe = new MerchantRecipe(
                        (new CustomItemStack(vanilla_recipe.getResult())),
                        ((MerchantRecipe) vanilla_recipe).getUses(), ((MerchantRecipe) vanilla_recipe).getMaxUses(),
                        ((MerchantRecipe) vanilla_recipe).hasExperienceReward(),
                        ((MerchantRecipe) vanilla_recipe).getVillagerExperience(),
                        ((MerchantRecipe) vanilla_recipe).getPriceMultiplier());
                for (ItemStack ingredient : ((MerchantRecipe) vanilla_recipe).getIngredients()){
                    new_recipe.addIngredient((new CustomItemStack(ingredient)));
                }
                Bukkit.addRecipe(new_recipe);
            } else if (vanilla_recipe instanceof ShapedRecipe) {
                ShapedRecipe new_recipe = new ShapedRecipe(key, new CustomItemStack(vanilla_recipe.getResult()));

                new_recipe.shape(((ShapedRecipe) vanilla_recipe).getShape());

                boolean skip = false;
                for (Character recipeKey : ((ShapedRecipe)vanilla_recipe).getChoiceMap().keySet()){
                    RecipeChoice originalChoice = ((ShapedRecipe)vanilla_recipe).getChoiceMap().get(recipeKey);
                    if (originalChoice instanceof RecipeChoice.ExactChoice){
                        ArrayList<ItemStack> ingredients = new ArrayList<>();
                        for (ItemStack stack : ((RecipeChoice.ExactChoice) originalChoice).getChoices()){
                            ingredients.add(new CustomItemStack(stack));
                        }
                        new_recipe.setIngredient(recipeKey, new RecipeChoice.ExactChoice(ingredients));
                    } else if (originalChoice instanceof RecipeChoice.MaterialChoice){
                        PeakCraft.getPlugin().getLogger().info("Skipping recipe because it uses a material choice");
                        // do nothing for now
                        skip = true;
                    }
                }
                if (!skip){
                    Bukkit.addRecipe(new_recipe);
                }
            } else if (vanilla_recipe instanceof ShapelessRecipe) {
                net.minecraft.world.item.crafting.RecipeManager
                ShapelessRecipe new_recipe = new ShapelessRecipe(key,
                        (new CustomItemStack(vanilla_recipe.getResult())));
                new_recipe.setGroup(((ShapelessRecipe) vanilla_recipe).getGroup());

                for (ItemStack ingredient : ((ShapelessRecipe) vanilla_recipe).getIngredientList()){
                    new_recipe.addIngredient(new RecipeChoice.ExactChoice(new CustomItemStack(ingredient)));
                }

                // Register it in the spigot recipe system
                Bukkit.addRecipe(new_recipe);
            } else if (vanilla_recipe instanceof SmithingRecipe) {
                SmithingRecipe new_recipe = new SmithingRecipe(key,
                        (new CustomItemStack(vanilla_recipe.getResult())),
                        new RecipeChoice.ExactChoice((new CustomItemStack(((SmithingRecipe) vanilla_recipe).getBase().getItemStack()))),
                        new RecipeChoice.ExactChoice((new CustomItemStack(((SmithingRecipe) vanilla_recipe).getAddition().getItemStack()))));
                Bukkit.addRecipe(new_recipe);
            } else if (vanilla_recipe instanceof StonecuttingRecipe){
                StonecuttingRecipe new_recipe = new StonecuttingRecipe(key,
                        (new CustomItemStack(vanilla_recipe.getResult())),
                        new RecipeChoice.ExactChoice((new CustomItemStack(((StonecuttingRecipe) vanilla_recipe).getInput()))));
                Bukkit.addRecipe(new_recipe);
            }
        }

        // Register all recipes

        getLogger().info("Registering recipes:");
        RecipeManager.registerRecipes();*/

        // Register enchants

        getLogger().info("Registering enchants:");
        EnchantmentManager.registerEnchantments();

        // Register full set bonuses

        getLogger().info("Registering full set bonuses:");
        FullSetBonusManager.registerSet(new SpaceSuitFullSetBonus());

    }
    @Override
    public void onDisable() {
        getLogger().info("onDisable is called!");
    }

    public static void disable(){
        getPlugin().getServer().getPluginManager().disablePlugin(instance);
    }
}
