package io.github.mrriptide.peakcraft;

import com.mysql.cj.jdbc.MysqlConnectionPoolDataSource;
import com.mysql.cj.jdbc.MysqlDataSource;
import io.github.mrriptide.peakcraft.commands.*;
import io.github.mrriptide.peakcraft.items.ItemManager;
import io.github.mrriptide.peakcraft.items.abilities.AbilityManager;
import io.github.mrriptide.peakcraft.items.abilities.FlightFeatherAbility;
import io.github.mrriptide.peakcraft.items.abilities.InspectAbility;
import io.github.mrriptide.peakcraft.items.abilities.PotatoSwordAbility;
import io.github.mrriptide.peakcraft.items.enchantments.EnchantmentManager;
import io.github.mrriptide.peakcraft.items.fullsetbonus.FullSetBonusManager;
import io.github.mrriptide.peakcraft.items.fullsetbonus.SpaceSuitFullSetBonus;
import io.github.mrriptide.peakcraft.listeners.*;
import io.github.mrriptide.peakcraft.recipes.RecipeManager;
import io.github.mrriptide.peakcraft.recipes.ShapedNMSRecipe;
import org.bukkit.Bukkit;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.*;
import org.bukkit.plugin.java.JavaPlugin;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.logging.Level;

public class PeakCraft extends JavaPlugin {
    public static PeakCraft instance;

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
        ItemManager.loadNewItems();
        ItemManager.loadItems();

        // if there is a version mismatch check

        /*if (!Objects.equals(getConfig().getString("itemBukkitVersion"), Bukkit.getBukkitVersion())){
            getLogger().warning("The items are out of date, generating a list of new vanilla items and disabling the plugin" +
                    "\nWhen the items are updated, update itemBukkitVersion with \"" + Bukkit.getBukkitVersion() + "\"");

            var items = ItemManager.getItems();
            var newItems = new ArrayList<Material>();

            for (Material mat : Material.values()){
                if (!items.containsKey(mat.name().toUpperCase())){
                    newItems.add(mat);
                }
            }

            ItemManager.writeMaterialsToFile(newItems, "newItems - " + Bukkit.getBukkitVersion() + ".json");

            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }*/

        // Register event listeners

        getServer().getPluginManager().registerEvents(new EntityEventListener(), this);
        getServer().getPluginManager().registerEvents(new GUIEventListener(), this);
        getServer().getPluginManager().registerEvents(new DamageListener(), this);
        getServer().getPluginManager().registerEvents(new ChunkListener(), this);
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
