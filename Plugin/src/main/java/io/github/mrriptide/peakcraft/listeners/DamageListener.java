package io.github.mrriptide.peakcraft.listeners;

import io.github.mrriptide.peakcraft.PeakCraft;
import io.github.mrriptide.peakcraft.entity.EntityManager;
import io.github.mrriptide.peakcraft.entity.player.PlayerManager;
import io.github.mrriptide.peakcraft.entity.wrappers.CombatEntityWrapper;
import io.github.mrriptide.peakcraft.entity.wrappers.LivingEntityWrapper;
import io.github.mrriptide.peakcraft.exceptions.EntityException;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftCreature;
import org.bukkit.entity.LivingEntity;
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
        if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK || event.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK){
            //event.setDamage(0);
            return;
        }
        LivingEntityWrapper entity;

        if (event.getEntity() instanceof Player && !CitizensAPI.getNPCRegistry().isNPC(event.getEntity())){
            entity = PlayerManager.getPlayer((Player)event.getEntity());
        } else if (EntityManager.isCustomMob(event.getEntity())){
            try {
                entity = new LivingEntityWrapper((LivingEntity) event.getEntity());
            } catch (EntityException e) {
                PeakCraft.getPlugin().getLogger().warning("An entity was recognized as a custom mob but something failed in wrapping! Please report this to the developers");
                e.printStackTrace();
                return;
            }
        } else {
            return;
        }
        entity.processDamage(event.getDamage() * 5, event.getCause());
        event.setDamage(0);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
        if (event.getEntity().isInvulnerable()){
            return;
        }

        CombatEntityWrapper damagingEntity;
        if (event.getDamager() instanceof Player && !CitizensAPI.getNPCRegistry().isNPC(event.getDamager())){
            damagingEntity = PlayerManager.getPlayer((Player) event.getDamager());
        }
        else if (EntityManager.isCustomMob(event.getDamager())) {
            try {
                damagingEntity = new CombatEntityWrapper((LivingEntity) event.getEntity());
            } catch (EntityException e) {
                PeakCraft.getPlugin().getLogger().warning("An entity was recognized as a custom mob but something failed in wrapping! Please report this to the developers");
                e.printStackTrace();
                return;
            }
        } else {
            PeakCraft.getPlugin().getLogger().warning("Some unregistered entity tried dealing damage");
            return;
        }

        LivingEntityWrapper damagedEntity;
        if (event.getEntity() instanceof Player)
            damagedEntity = PlayerManager.getPlayer((Player) event.getEntity());
        else if (EntityManager.isCustomMob(event.getEntity()))
        {
            try {
                damagedEntity = new LivingEntityWrapper((LivingEntity) event.getEntity());
            } catch (EntityException e) {
                PeakCraft.getPlugin().getLogger().warning("An entity was recognized as a custom mob but something failed in wrapping! Please report this to the developers");
                e.printStackTrace();
                return;
            }
        } else {
            PeakCraft.getPlugin().getLogger().warning("Some entity tried damaging an unregistered entity");
            return;
        }
        damagedEntity.processAttack(damagingEntity);

        event.setDamage(0);
    }
}
