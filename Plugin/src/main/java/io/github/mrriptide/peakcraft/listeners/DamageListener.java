package io.github.mrriptide.peakcraft.listeners;

import io.github.mrriptide.peakcraft.PeakCraft;
import io.github.mrriptide.peakcraft.entity.EntityManager;
import io.github.mrriptide.peakcraft.entity.player.PlayerManager;
import io.github.mrriptide.peakcraft.entity.wrappers.CombatEntityWrapper;
import io.github.mrriptide.peakcraft.entity.wrappers.LivingEntityWrapper;
import io.github.mrriptide.peakcraft.exceptions.EntityException;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Arrays;

public class DamageListener implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event){
        if (event.getEntity().isInvulnerable()){
            return;
        }
        if (Arrays.asList(EntityDamageEvent.DamageCause.ENTITY_ATTACK, EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK, EntityDamageEvent.DamageCause.CUSTOM).contains(event.getCause()) ){
            event.setDamage(0);
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
        event.setCancelled(true);
        entity.processDamage(event.getDamage() * 5, event.getCause());
        event.setDamage(0);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity().isInvulnerable()) {
            return;
        }

        CombatEntityWrapper damagingEntity;
        // check if the damaging entity is a player
        if (EntityManager.isPlayer(event.getDamager())) {
            damagingEntity = PlayerManager.getPlayer((Player) event.getDamager());
        } else if (EntityManager.isCustomMob(event.getDamager())) {
            try {
                damagingEntity = new CombatEntityWrapper((LivingEntity) event.getDamager());
            } catch (EntityException e) {
                PeakCraft.getPlugin().getLogger().warning("An entity was recognized as a custom mob but something failed in wrapping! Please report this to the developers");
                e.printStackTrace();
                return;
            }
        } else {
            PeakCraft.getPlugin().getLogger().warning("Some unregistered entity tried dealing damage");
            return;
        }

        // check if the damaged entity is a player
        LivingEntityWrapper damagedEntity;
        if (EntityManager.isPlayer(event.getEntity())){
            damagedEntity = PlayerManager.getPlayer((Player) event.getEntity());
        } else if (EntityManager.isCustomMob(event.getEntity())) {
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
        event.setCancelled(true);
        damagingEntity.updateAttributes();
        damagedEntity.updateAttributes();
        damagedEntity.processAttack(damagingEntity);
        event.setDamage(0);
    }
}
