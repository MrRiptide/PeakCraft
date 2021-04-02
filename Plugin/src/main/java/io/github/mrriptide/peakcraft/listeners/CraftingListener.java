package io.github.mrriptide.peakcraft.listeners;

import io.github.mrriptide.peakcraft.PeakCraft;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.Recipe;

public class CraftingListener implements Listener {
    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent e){
        Recipe recipe = e.getRecipe();

        PeakCraft.getPlugin().getLogger().info(recipe.toString());

        for (HumanEntity entity : e.getInventory().getViewers()){
            entity.sendMessage("event");
        }
    }
}
