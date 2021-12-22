package io.github.mrriptide.peakcraft.actions;

import io.github.mrriptide.peakcraft.util.Attribute;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashMap;

public class Damage {
    protected HashMap<DamageType, Attribute> damages;
    protected HashMap<DamageType, Attribute> protections;
    protected double multiplier = 1;

    public Damage(double amount, EntityDamageEvent.DamageCause cause) {
        damages = new HashMap<>();
        protections = new HashMap<>();
        switch (cause){
            case PROJECTILE -> damages.put(DamageType.PROJECTILE, new Attribute(amount));
            case FALL,FALLING_BLOCK,CONTACT,FLY_INTO_WALL,CRAMMING,DROWNING,SUFFOCATION,FREEZE -> damages.put(DamageType.ENVIRONMENTAL, new Attribute(amount));
            case MAGIC,WITHER,DRAGON_BREATH,THORNS -> damages.put(DamageType.MAGIC, new Attribute(amount));
            case ENTITY_ATTACK,ENTITY_SWEEP_ATTACK -> damages.put(DamageType.MELEE, new Attribute(amount));
            case FIRE,FIRE_TICK,LAVA,HOT_FLOOR,LIGHTNING,MELTING -> damages.put(DamageType.FIRE, new Attribute(amount));
            case BLOCK_EXPLOSION,ENTITY_EXPLOSION -> damages.put(DamageType.EXPLOSION, new Attribute(amount));
            default -> damages.put(DamageType.GENERIC, new Attribute(amount));
        }
    }

    public Attribute getDamage(DamageType type){
        if (!damages.containsKey(type))
            damages.put(type, new Attribute(0));
        return damages.get(type);
    }

    public Attribute getProtection(DamageType type){
        if (!protections.containsKey(type))
            protections.put(type, new Attribute(0));
        return protections.get(type);
    }

    public void addMultiplier(double multi){
        this.multiplier += multi;
    }

    public double finalizeDamage(){
        Attribute damage = new Attribute(0);
        Attribute protection = protections.getOrDefault(DamageType.GENERIC, new Attribute(0));
        // later I should figure out how to have a special type of protection only apply to a certain portion of the damage
        for (DamageType type : damages.keySet()){
            if (type == DamageType.GENERIC){
                damage.addAdditive(damages.get(type).getAdditive());
                damage.addMulti(damages.get(type).getMulti());
            } else {
                if (damages.get(type).getFinal() > 0){
                    protection.addAdditive(protections.getOrDefault(type, new Attribute(0)).getAdditive());
                    protection.addMulti(protections.getOrDefault(type, new Attribute(0)).getMulti());
                    damage.addAdditive(damages.get(type).getFinal());
                }
            }
        }
        double general_protection = protection.getFinal();
        damage.addMulti(-(general_protection/(general_protection + 50)));
        return damage.getFinal() * multiplier;
    }

    private double applyProtection(double rawDamage, double protection){
        return rawDamage * (1-(protection/(protection + 50)));
    }

    public enum DamageType {
        MELEE, PROJECTILE, ENVIRONMENTAL, FIRE, MAGIC, EXPLOSION, GENERIC
    }
}
