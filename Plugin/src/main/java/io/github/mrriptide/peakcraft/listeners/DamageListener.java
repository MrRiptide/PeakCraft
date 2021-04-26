package io.github.mrriptide.peakcraft.listeners;

import io.github.mrriptide.peakcraft.PeakCraft;
import io.github.mrriptide.peakcraft.entity.CustomDamageableEntity;
import io.github.mrriptide.peakcraft.entity.CustomHostileEntity;
import io.github.mrriptide.peakcraft.items.Item;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageListener implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event){
        PeakCraft.getPlugin().getLogger().info("EntityDamageEventCalled " + event.getDamage());
        if (!(event.getEntity() instanceof CustomDamageableEntity)){
            event.setCancelled(true);
        }
        if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK || event.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK){
            event.setDamage(0);
            return;
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        if (!(event.getEntity() instanceof CustomDamageableEntity || event.getEntity() instanceof Player)){
            event.setCancelled(true);
        }
        PeakCraft.getPlugin().getLogger().info("Damaged by entity " + event.getDamage());
        Item weapon = null;
        double strength = 0;
        if (event.getDamager() instanceof CustomHostileEntity){
            Bukkit.broadcastMessage("Is custom hostile entity");
            weapon = ((CustomHostileEntity)event.getDamager()).getWeapon();
            strength = ((CustomHostileEntity)event.getDamager()).getStrength();
        } else if (event.getDamager() instanceof Player){
            Bukkit.broadcastMessage("Is player");
            weapon = new Item(((Player) event.getDamager()).getInventory().getItemInMainHand());
            strength = 10;
        }

        if (event.getEntity() instanceof CustomDamageableEntity)
            ((CustomDamageableEntity)event.getEntity()).processAttack(weapon, strength);
        else if (event.getEntity() instanceof Player){
            io.github.mrriptide.peakcraft.Player player = new io.github.mrriptide.peakcraft.Player((Player) event.getEntity());
            player.processAttack(weapon, strength);
        }

        event.setDamage(0);
    }
}
