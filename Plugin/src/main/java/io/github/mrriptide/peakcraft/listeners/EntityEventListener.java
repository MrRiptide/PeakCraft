package io.github.mrriptide.peakcraft.listeners;

import io.github.mrriptide.peakcraft.ItemManager;
import io.github.mrriptide.peakcraft.PeakCraft;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class EntityEventListener implements Listener {

    public EntityEventListener(){
        PeakCraft.getPlugin().getLogger().info("Entity Event Listener initialized");
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent e){
        LivingEntity entity = e.getEntity();
        if (entity instanceof Player){
            Player player = (Player) entity;

            ItemStack originalItem = e.getItem().getItemStack();

            ItemStack newItem = ItemManager.ConvertItem(originalItem);


            //player.getInventory().addItem(newItem);
            e.getItem().setItemStack(newItem);
        }
    }
}
