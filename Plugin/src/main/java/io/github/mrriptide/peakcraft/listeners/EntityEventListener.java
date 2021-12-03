package io.github.mrriptide.peakcraft.listeners;

import io.github.mrriptide.peakcraft.PeakCraft;
import io.github.mrriptide.peakcraft.entity.EntityManager;
import io.github.mrriptide.peakcraft.entity.player.PlayerManager;
import io.github.mrriptide.peakcraft.entity.player.PlayerWrapper;
import io.github.mrriptide.peakcraft.entity.wrappers.LivingEntityWrapper;
import io.github.mrriptide.peakcraft.exceptions.EntityException;
import io.github.mrriptide.peakcraft.exceptions.ItemException;
import io.github.mrriptide.peakcraft.recipes.CustomItemStack;
import io.github.mrriptide.peakcraft.util.CustomColors;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import java.util.Arrays;
import java.util.List;

public class EntityEventListener implements Listener {

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent e){
        org.bukkit.entity.LivingEntity entity = e.getEntity();
        if (entity instanceof Player){
            Player player = (Player) entity;

            try{
                e.getItem().setItemStack(new CustomItemStack(e.getItem().getItemStack()));
            } catch (ItemException error){
                player.sendMessage(CustomColors.ERROR + "That item had an invalid id tag, please report this!");
                PeakCraft.getPlugin().getLogger().warning("Player " + player.getName() + " picked up an item with an invalid id!");
            }
        }
    }

    @EventHandler
    public void onRegainHealth(EntityRegainHealthEvent e){
        if (e.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED){
            e.setCancelled(true);
        } else if (e.getEntity() instanceof Player && !(e.getEntity().hasMetadata("NPC"))){
            PlayerWrapper playerWrapper = PlayerManager.getPlayer((Player) e.getEntity());
            playerWrapper.regenHealth(e.getAmount());
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
        if (PeakCraft.generatingEntityDatabase)
            return;
        // checks if it is an NPC, if so ignore because it should already be a custom one
        if (e.getEntity().hasMetadata("NPC")){
            return;
        }
        if (((CraftEntity)e.getEntity()).getHandle() instanceof ArmorStand || !(e.getEntity() instanceof LivingEntity)){
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
        }
    }
}
