package io.github.mrriptide.peakcraft.listeners;

import io.github.mrriptide.peakcraft.recipes.Recipe;
import io.github.mrriptide.peakcraft.recipes.ShapedRecipe;
import io.github.mrriptide.peakcraft.recipes.RecipeManager;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

public class CraftingListener implements Listener {
    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent e){
        org.bukkit.inventory.Recipe source_recipe = e.getRecipe();
        Recipe recipe = null;

        if (source_recipe != null){
            recipe = RecipeManager.getRecipe(((Keyed) source_recipe).getKey().getKey());
        }
        if (recipe == null){
            recipe = RecipeManager.getMatch(e.getInventory().getMatrix());
        }

        if (recipe == null){
            return;
        }

        // TODO: add something to transfer upgrades/attributes from item, for now, just setting the output as a boat to test

        e.getInventory().setResult(recipe.getResult().getItemStack());
    }

    @EventHandler
    public void onCraftingClick(InventoryClickEvent e){
        if (e.getClickedInventory() instanceof CraftingInventory){
            CraftingInventory craftingInventory = (CraftingInventory) e.getClickedInventory();
            
        }
    }
}
