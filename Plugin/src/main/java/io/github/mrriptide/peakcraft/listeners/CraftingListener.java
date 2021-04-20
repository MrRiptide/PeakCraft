package io.github.mrriptide.peakcraft.listeners;

import io.github.mrriptide.peakcraft.PeakCraft;
import io.github.mrriptide.peakcraft.recipes.*;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class CraftingListener implements Listener {

    /*@EventHandler(priority = EventPriority.HIGHEST)
    public void onCraftingClick(InventoryClickEvent e){
        if (e.getClickedInventory() instanceof CraftingInventory){
            CraftingInventory craftingInventory = (CraftingInventory) e.getClickedInventory();


            updateCraft((CraftingInventory) e.getClickedInventory());
        }
    }*/

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent e){
        //updateCraft(e.getInventory());
    }

    private void updateCraft(CraftingInventory craftingInventory){
        Recipe recipe = getRecipe(craftingInventory);

        if (recipe == null){
            return;
        }

        // TODO: add something to transfer upgrades/attributes from item, for now, just setting the output as a boat to test

        //craftingInventory.setResult(recipe.getResult().getItemStack());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCraftItem(CraftItemEvent e){
        PeakCraft.getPlugin().getLogger().info("hi?");
        CraftingInventory craftingInventory = e.getInventory();
        ItemStack[] ingredients = craftingInventory.getMatrix();
        Recipe recipe = getRecipe(craftingInventory);

        if (recipe instanceof ShapedRecipe){
            ShapedRecipe shapedRecipe = (ShapedRecipe) recipe;
            ArrayList<Integer> valid_cols = new ArrayList<>();
            ArrayList<Integer> valid_rows = new ArrayList<>();

            // loop through two dimensions
            for (int i = 0; i <= 2; i++){
                boolean row_contains_item = false;
                boolean col_contains_item = false;
                for (int j = 0; j <= 2; j++){
                    // checks if column is empty
                    if (ingredients[j*3 + i] != null && ingredients[j*3 + i].getType() != Material.AIR){
                        col_contains_item = true;
                    }
                    // checks if row is empty
                    if (ingredients[i*3 + j] != null && ingredients[i*3 + j].getType() != Material.AIR){
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

            for (int r = 0; r < shapedRecipe.getShape().length; r++) {
                for (int c = 0; r < shapedRecipe.getShape()[0].length(); c++) {
                    ingredients[valid_rows.get(r) * 3 + valid_cols.get(c)].setAmount(ingredients[valid_rows.get(r) * 3 + valid_cols.get(c)].getAmount() - shapedRecipe.getIngredients().get(shapedRecipe.getShape()[r].charAt(c)).getCount());
                }
            }
        } else if (recipe instanceof ShapelessRecipe){
            ShapelessRecipe shapelessRecipe = (ShapelessRecipe) recipe;
            ArrayList<RecipeItem> recipeItems = shapelessRecipe.getIngredients();
            for (int i = 0; i < ingredients.length; i++){
                if (ingredients[i] == null){
                    continue;
                }
                RecipeItem craftItem = new RecipeItem(ingredients[i]);
                for (RecipeItem recipeItem : recipeItems){
                    if (craftItem.getCount() >= recipeItem.getCount() && craftItem.getCount() >= recipeItem.getCount() && (craftItem.getId().equals(recipeItem.getId())
                            || (!recipeItem.getOreDict().equals("") && recipeItem.getOreDict().equals(craftItem.getOreDict())))){
                        ingredients[i].setAmount(ingredients[i].getAmount() - recipeItem.getCount());
                        recipeItems.remove(recipeItem);
                        break;
                    }
                }
            }
        }

        e.setCancelled(true);
        craftingInventory.setMatrix(ingredients);
    }

    private Recipe getRecipe(CraftingInventory craftingInventory){
        org.bukkit.inventory.Recipe source_recipe = craftingInventory.getRecipe();
        Recipe recipe = null;

        if (source_recipe != null){
            recipe = RecipeManager.getRecipe(((Keyed) source_recipe).getKey().getKey());
        }
        if (recipe == null){
            recipe = RecipeManager.getMatch(craftingInventory.getMatrix());
        }

        return recipe;
    }
}
