package io.github.mrriptide.peakcraft.entity;

import io.github.mrriptide.peakcraft.entity.player.PlayerWrapper;
import io.github.mrriptide.peakcraft.items.EnchantableItem;
import io.github.mrriptide.peakcraft.items.Item;
import io.github.mrriptide.peakcraft.util.CustomColors;
import io.github.mrriptide.peakcraft.util.HoloDisplay;
import io.github.mrriptide.peakcraft.util.PersistentDataManager;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.persistence.PersistentDataType;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class LivingEntity extends PathfinderMob {
    protected String name;
    protected final String id;
    protected double health;
    protected double maxHealth;

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

    protected double defense;

    protected LivingEntity(String id, EntityType<? extends PathfinderMob> type, Level world) {
        super(type, world);
        setName("Unnamed Mob");
        this.id = id;
        updateName();
        initPathfinder();
    }

    public abstract void initPathfinder() ;

    public void updateName(){
        this.setCustomName(new TextComponent(name + " " + CustomColors.HEALTH + ((int)health) + " ❤"));
        this.setCustomNameVisible(true);
    }

    public void setName(String name){
        this.name = name;

        updateName();
    }

    public void updateEntity(){
        this.updateName();
        this.health = Math.max(0, Math.min(health, maxHealth));

        ((org.bukkit.entity.LivingEntity)this.getBukkitEntity()).setHealth(Math.min(this.health/this.maxHealth*20.0, 20.0));
        PersistentDataManager.setValue(this.getBukkitEntity(), "health", this.health);
        PersistentDataManager.setValue(this.getBukkitEntity(), "maxHealth", this.maxHealth);
        PersistentDataManager.setValue(this.getBukkitEntity(), "defense", this.defense);
    }

    public void applyNBT(){
        PersistentDataManager.setValue(this.getBukkitEntity(), "name", name);
        PersistentDataManager.setValue(this.getBukkitEntity(), "id", id);
        PersistentDataManager.setValue(this.getBukkitEntity(), "health", health);
        PersistentDataManager.setValue(this.getBukkitEntity(), "maxHealth", maxHealth);
        PersistentDataManager.setValue(this.getBukkitEntity(), "defense", defense);
    }

    public void processAttack(CombatEntity attacker){
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

        HoloDisplay damageDisplay = new HoloDisplay(this.getBukkitEntity().getLocation().add(Math.random()*1-0.5, Math.random()*1-1.5, Math.random()*1 -0.5));
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

    public void processAttack(EnchantableItem weapon, double strength){
        double damage = 1;
        if (weapon != null){
            damage = weapon.getBakedAttribute("damage");
        }

        health -= damage * (1 + strength * 0.05);
        updateName();
    }

    public ArrayList<String> getData(){
        ArrayList<String> data = new ArrayList<String>();
        data.add("health: " + health);
        data.add("maxHealth: " + maxHealth);
        data.add("id: " + id);
        data.add("name: " + name);
        data.add("defense: " + defense);
        data.add("Target: " + (this.getTarget() != null ? this.getTarget().getScoreboardName() : CustomColors.ERROR + "null"));
        return data;
    }
}
