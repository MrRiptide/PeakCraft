package io.github.mrriptide.peakcraft.listeners;

import io.github.mrriptide.peakcraft.PeakCraft;
import io.github.mrriptide.peakcraft.entity.BruteEntity;
import io.github.mrriptide.peakcraft.entity.HostileEntity;
import io.github.mrriptide.peakcraft.entity.LivingEntity;
import io.github.mrriptide.peakcraft.entity.CombatEntity;
import io.github.mrriptide.peakcraft.entity.wrappers.CombatEntityWrapper;
import io.github.mrriptide.peakcraft.entity.wrappers.HealthEntityWrapper;
import io.github.mrriptide.peakcraft.entity.wrappers.PlayerWrapper;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftCreature;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageListener implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event){
        if (event.getEntity().isInvulnerable()){
            return;
        }
        if (!(event.getEntity() instanceof LivingEntity)){
            //event.setCancelled(true);
        }
        if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK || event.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK){
            //event.setDamage(0);
            return;
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        if (event.getEntity().isInvulnerable()){
            return;
        }
        CombatEntity damagingEntity;
        if (event.getDamager() instanceof Player){
            damagingEntity = new PlayerWrapper((Player) event.getDamager());
        }
        else if (event.getEntity() instanceof LivingEntity) {
            Bukkit.broadcastMessage("Is custom hostile entity");
            damagingEntity = ((HostileEntity)event.getDamager());
        } else {
            PeakCraft.getPlugin().getLogger().warning("Some unregistered entity tried dealing damage");
            return;
        }

        LivingEntity damagedEntity;
        if (event.getEntity() instanceof Player)
            damagedEntity = new PlayerWrapper((Player) event.getEntity());
        else if (event.getEntity() instanceof LivingEntity) {
            damagedEntity = (LivingEntity) ((CraftCreature)event.getEntity()).getHandle();
        } else {
            PeakCraft.getPlugin().getLogger().warning("Some entity tried damaging an unregistered entity");
            return;
        }
        damagedEntity.processAttack(damagingEntity);

        event.setDamage(0);
    }
}
