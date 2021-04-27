package io.github.mrriptide.peakcraft.listeners;

import io.github.mrriptide.peakcraft.PeakCraft;
import io.github.mrriptide.peakcraft.entity.CustomDamageableEntity;
import io.github.mrriptide.peakcraft.entity.CustomHostileEntity;
import io.github.mrriptide.peakcraft.entity.wrappers.CombatEntityWrapper;
import io.github.mrriptide.peakcraft.entity.wrappers.EntityWrapper;
import io.github.mrriptide.peakcraft.entity.wrappers.HealthEntityWrapper;
import io.github.mrriptide.peakcraft.entity.wrappers.PlayerWrapper;
import io.github.mrriptide.peakcraft.items.Item;
import net.minecraft.server.v1_16_R3.EntityLiving;
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
            //event.setCancelled(true);
        }
        if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK || event.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK){
            //event.setDamage(0);
            return;
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        PeakCraft.getPlugin().getLogger().info("Damaged by entity " + event.getDamage());
        CombatEntityWrapper damagingEntity;
        if (event.getDamager() instanceof Player){
            Bukkit.broadcastMessage("Is player");
            damagingEntity = new PlayerWrapper((Player) event.getDamager());
        }
        else {
            Bukkit.broadcastMessage("Is custom hostile entity");
            damagingEntity = new CombatEntityWrapper(event.getDamager());
        }

        HealthEntityWrapper damagedEntity;
        if (event.getEntity() instanceof Player)
            damagedEntity = new PlayerWrapper((Player) event.getEntity());
        else {
            damagedEntity = new HealthEntityWrapper(event.getEntity());
        }
        damagedEntity.processAttack(damagingEntity);

        event.setDamage(0.1);
    }
}
