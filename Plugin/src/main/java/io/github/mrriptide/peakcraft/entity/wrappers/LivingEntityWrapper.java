package io.github.mrriptide.peakcraft.entity.wrappers;

import io.github.mrriptide.peakcraft.entity.EntityManager;
import io.github.mrriptide.peakcraft.entity.data.LivingEntityData;
import io.github.mrriptide.peakcraft.entity.player.PlayerWrapper;
import io.github.mrriptide.peakcraft.exceptions.EntityException;
import io.github.mrriptide.peakcraft.items.EnchantableItem;
import io.github.mrriptide.peakcraft.items.Item;
import io.github.mrriptide.peakcraft.util.CustomColors;
import io.github.mrriptide.peakcraft.util.HoloDisplay;
import io.github.mrriptide.peakcraft.util.PersistentDataManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.persistence.PersistentDataType;

import java.text.NumberFormat;
import java.util.ArrayList;

public class LivingEntityWrapper {
    protected Entity entity;
    protected String entityType;
    protected String entityMode;
    protected String name;
    protected String id;
    protected double health;
    protected double maxHealth;
    protected double defense;
    protected boolean showHealth = true;

    protected LivingEntityWrapper(){
    }

    public LivingEntityWrapper(LivingEntity entity) throws EntityException {
        this.entity = entity;
        this.entityMode = PersistentDataManager.getValueOrDefault(entity, PersistentDataType.STRING, "mode", "none");
        this.id = PersistentDataManager.getValueOrDefault(entity, PersistentDataType.STRING, "id", entity.getName());
        if (this.entityMode.equals("none")){
            LivingEntityData entityData = EntityManager.getEntity(this.id);
            this.entityType = entityData.getType();
            this.name = entityData.getName();
            this.health = entityData.getMaxHealth();
            this.maxHealth = entityData.getMaxHealth();
            this.defense = entityData.getDefense();
            applyNBT();
            updateEntity();
        }
        this.entityType = PersistentDataManager.getValueOrDefault(entity, PersistentDataType.STRING, "type",
                this instanceof CombatEntityWrapper ? "normal_hostile" : "passive");
        this.name = PersistentDataManager.getValueOrDefault(entity, PersistentDataType.STRING, "name", entity.getName());
        this.health = entity.getHealth();
        this.maxHealth = PersistentDataManager.getValueOrDefault(entity, PersistentDataType.DOUBLE, "maxHealth", 0.0);
        this.defense = PersistentDataManager.getValueOrDefault(entity, PersistentDataType.DOUBLE, "defense", 0.0);
        updateName();
    }

    public void updateName(){
        ChatColor name_color = switch (entityType) {
            case ("boss") -> CustomColors.BOSS_ENTITY;
            case ("npc") -> CustomColors.NPC;
            default -> CustomColors.BASIC_ENTITY;
        };
        entity.setCustomName(name_color + name + " " + CustomColors.HEALTH + ((int)health) + " ❤");
        entity.setCustomNameVisible(true);
    }

    public void setName(String name){
        this.name = name;

        updateName();
    }

    public void updateEntity(){
        if (!(this instanceof PlayerWrapper))
            this.updateName();
        this.health = Math.max(0, Math.min(health, maxHealth));

        PersistentDataManager.setValue(entity, "health", this.health);
        if (!(this instanceof PlayerWrapper)){
            ((org.bukkit.entity.LivingEntity)entity).getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(this.maxHealth);
            ((org.bukkit.entity.LivingEntity)entity).setHealth(this.health);
        }
        else {
            ((org.bukkit.entity.LivingEntity)entity).setHealth(Math.min(this.health / this.maxHealth * 20.0, 20.0));
        }
        PersistentDataManager.setValue(entity, "maxHealth", this.maxHealth);
        PersistentDataManager.setValue(entity, "defense", this.defense);
    }

    public void applyNBT(){
        PersistentDataManager.setValue(entity, "type", entityType);
        PersistentDataManager.setValue(entity, "name", name);
        PersistentDataManager.setValue(entity, "id", id);
        PersistentDataManager.setValue(entity, "health", health);
        PersistentDataManager.setValue(entity, "maxHealth", maxHealth);
        PersistentDataManager.setValue(entity, "defense", defense);
    }

    public void processAttack(EnchantableItem weapon, double strength){
        double damage = 1;
        if (weapon != null){
            damage = weapon.getBakedAttribute("damage");
        }

        health -= damage * (1 + strength * 0.05);
        updateName();
    }

    public void processAttack(CombatEntityWrapper attacker){
        double damage;

        Item weapon = attacker.getWeapon();

        if (!(weapon instanceof EnchantableItem)){
            damage = 10;
        } else{
            ((EnchantableItem)weapon).bakeAttributes();
            damage = (((EnchantableItem)weapon).getBakedAttribute("damage")!=0) ? ((EnchantableItem)weapon).getBakedAttribute("damage") : 10;
        }
        double multiplier = 1.0;
        if (attacker instanceof PlayerWrapper){
            if ((((PlayerWrapper) attacker).getSource()).getAttackCooldown() == 1.0 && Math.random() <= ((PlayerWrapper)attacker).getCritChance()){
                multiplier = 1 + ((PlayerWrapper)attacker).getCritDamage();
            } else {
                multiplier = (((PlayerWrapper) attacker).getSource()).getAttackCooldown();
            }
        }

        double damagePotential = damage*(1+0.05*attacker.strength)/(1+defense*0.05) * multiplier;

        HoloDisplay damageDisplay = new HoloDisplay(entity.getLocation().add(Math.random()*1-0.5, Math.random()*1-1.5, Math.random()*1 -0.5));
        ChatColor damageColor;
        if (multiplier < 1){
            damageColor = CustomColors.WEAK_ATTACK;
        } else if (multiplier == 1){
            damageColor = CustomColors.NORMAL_ATTACK;
        } else {
            damageColor = CustomColors.CRIT_ATTACK;
        }

        NumberFormat format = NumberFormat.getInstance();
        format.setGroupingUsed(true);

        damageDisplay.showThenDie(damageColor + "" + format.format((int)damagePotential), 40);

        processDamage(damagePotential);
    }

    public void processDamage(double amount, EntityDamageEvent.DamageCause cause){
        processDamage(amount);
    }

    public void processDamage(double amount){
        this.health = Math.max(health - amount, 0);
        ((LivingEntity)entity).damage(0.001);
        updateEntity();

    }

    public void regenHealth(double amount){
        this.health = Math.min(health + amount, maxHealth);
        updateEntity();
    }

    public void setMaxHealth(double maxHealth){
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        updateName();
    }

    public ArrayList<String> getData(){
        ArrayList<String> data = new ArrayList<String>();
        data.add("wrapped: false");
        data.add("health: " + health);
        data.add("maxHealth: " + maxHealth);
        data.add("id: " + id);
        data.add("name: " + name);
        data.add("defense: " + defense);
        return data;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setShowHealth(boolean showHealth){
        this.showHealth = showHealth;
    }

    public String getEntityName() {
        return name;
    }

    public String getEntityId() {
        return id;
    }

    public double getEntityHealth() {
        return health;
    }

    public double getEntityMaxHealth() {
        return maxHealth;
    }

    public double getEntityDefense() {
        return defense;
    }
}
