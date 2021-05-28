package io.github.mrriptide.peakcraft.listeners;

import io.github.mrriptide.peakcraft.entity.PlayerWrapper;
import io.github.mrriptide.peakcraft.items.ItemManager;
import io.github.mrriptide.peakcraft.PeakCraft;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftCreature;
import io.github.mrriptide.peakcraft.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class EntityEventListener implements Listener {

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent e){
        org.bukkit.entity.LivingEntity entity = e.getEntity();
        if (entity instanceof Player){
            Player player = (Player) entity;

            ItemStack originalItem = e.getItem().getItemStack();

            ItemStack newItem = ItemManager.convertItem(originalItem).getItemStack();


            //player.getInventory().addItem(newItem);
            e.getItem().setItemStack(newItem);
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e){
        PlayerWrapper playerWrapper = new PlayerWrapper(e.getPlayer());
        playerWrapper.resetStats();
    }

    @EventHandler
    public void onRegainHealth(EntityRegainHealthEvent e){
        if (e.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED){
            e.setCancelled(true);
        } else if (e.getEntity() instanceof Player){
            PlayerWrapper wrapper = new PlayerWrapper((Player)e.getEntity());
            wrapper.regenHealth(e.getAmount());
        } else if (((CraftCreature)e.getEntity()).getHandle() instanceof LivingEntity){
            LivingEntity entity = (LivingEntity) ((CraftCreature)e.getEntity()).getHandle();
            entity.regenHealth(e.getAmount());
        } else {
            return;
        }
        e.setAmount(0);
    }
}
