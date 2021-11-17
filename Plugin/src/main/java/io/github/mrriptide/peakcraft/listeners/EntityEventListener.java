package io.github.mrriptide.peakcraft.listeners;

import io.github.mrriptide.peakcraft.entity.EntityManager;
import io.github.mrriptide.peakcraft.entity.player.PlayerWrapper;
import io.github.mrriptide.peakcraft.exceptions.EntityException;
import io.github.mrriptide.peakcraft.exceptions.ItemException;
import io.github.mrriptide.peakcraft.items.ItemManager;
import io.github.mrriptide.peakcraft.PeakCraft;
import io.github.mrriptide.peakcraft.util.CustomColors;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftCreature;
import io.github.mrriptide.peakcraft.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class EntityEventListener implements Listener {

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent e){
        org.bukkit.entity.LivingEntity entity = e.getEntity();
        if (entity instanceof Player){
            Player player = (Player) entity;

            ItemStack originalItem = e.getItem().getItemStack();

            try{
                ItemStack newItem = ItemManager.convertItem(originalItem).getItemStack();
                e.getItem().setItemStack(newItem);
            } catch (ItemException error){
                player.sendMessage(CustomColors.ERROR + "That item had an invalid id tag, please report this!");
                PeakCraft.getPlugin().getLogger().warning("Player " + player.getName() + " picked up an item with an invalid id!");
            }
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

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent e){
        if (EntityManager.isCustomMob(e.getEntity())){
            return;
        }

        e.setCancelled(true);

        try {
            EntityManager.convertEntity(e.getEntity(), e.getLocation(), true);
        } catch (EntityException ex) {
            ex.printStackTrace();
        }
    }
}
