package io.github.mrriptide.peakcraft.listeners;

import io.github.mrriptide.peakcraft.PeakCraft;
import io.github.mrriptide.peakcraft.entity.EntityManager;
import io.github.mrriptide.peakcraft.entity.wrappers.LivingEntityWrapper;
import io.github.mrriptide.peakcraft.entity.player.PlayerWrapper;
import io.github.mrriptide.peakcraft.exceptions.EntityException;
import io.github.mrriptide.peakcraft.exceptions.ItemException;
import io.github.mrriptide.peakcraft.items.ItemManager;
import io.github.mrriptide.peakcraft.util.CustomColors;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

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
        try {
            PlayerWrapper playerWrapper = new PlayerWrapper(e.getPlayer());
            playerWrapper.resetStats();
        } catch (EntityException ex) {
            PeakCraft.getPlugin().getLogger().warning("Player respawned but could not be wrapped!");
            ex.printStackTrace();
        }
    }

    @EventHandler
    public void onRegainHealth(EntityRegainHealthEvent e){
        if (e.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED){
            e.setCancelled(true);
        } else if (e.getEntity() instanceof Player && !(e.getEntity().hasMetadata("NPC"))){
            try {
                PlayerWrapper playerWrapper = new PlayerWrapper((Player) e.getEntity());
                playerWrapper.regenHealth(e.getAmount());
            } catch (EntityException ex) {
                PeakCraft.getPlugin().getLogger().warning("Player regained health but could not be wrapped!");
                ex.printStackTrace();
            }
        } else if (EntityManager.isCustomMob(e.getEntity())){
            LivingEntityWrapper entity = null;
            try {
                entity = new LivingEntityWrapper((LivingEntity) e.getEntity());
            } catch (EntityException ex) {
                ex.printStackTrace();
                return;
            }
            entity.regenHealth(e.getAmount());
        } else {
            return;
        }
        e.setAmount(0);
    }

    @EventHandler
    public void onEntitySpawn(CreatureSpawnEvent e){
        return;
        /*// checks if it is an NPC, if so ignore because it should already be a custom one
        if (e.getEntity().hasMetadata("NPC")){
            return;
        }

        List<CreatureSpawnEvent.SpawnReason> staticSpawnReasons = Arrays.asList(
                CreatureSpawnEvent.SpawnReason.COMMAND,
                CreatureSpawnEvent.SpawnReason.CUSTOM,
                CreatureSpawnEvent.SpawnReason.EGG,
                CreatureSpawnEvent.SpawnReason.SHEARED,
                CreatureSpawnEvent.SpawnReason.SHOULDER_ENTITY
                );

        try {
            // if it isn't a custom npc, pass it off to the spawn converter
            e.setCancelled(EntityManager.convertSpawn(e.getEntity(), e.getLocation(),
                    !staticSpawnReasons.contains(e.getSpawnReason()), e.getSpawnReason()));
        } catch (EntityException ex) {
            ex.printStackTrace();
        }*/
    }
}
