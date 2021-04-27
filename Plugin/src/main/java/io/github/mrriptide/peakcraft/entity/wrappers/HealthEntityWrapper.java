package io.github.mrriptide.peakcraft.entity.wrappers;

import io.github.mrriptide.peakcraft.items.Item;
import io.github.mrriptide.peakcraft.util.PersistentDataManager;
import net.minecraft.server.v1_16_R3.ChatComponentText;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftCreature;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;

public class HealthEntityWrapper extends EntityWrapper{
    protected double health;
    protected double maxHealth;
    protected double defense;

    public HealthEntityWrapper(Entity entity){
        super(entity);
        this.health = PersistentDataManager.getValueOrDefault(entity, PersistentDataType.DOUBLE, "health", 0.0);
        this.maxHealth = PersistentDataManager.getValueOrDefault(entity, PersistentDataType.DOUBLE, "maxHealth", 0.0);
        this.defense = PersistentDataManager.getValueOrDefault(entity, PersistentDataType.DOUBLE, "defense", 0.0);
    }

    public HealthEntityWrapper(CraftCreature creature){
        this(creature.getHandle().getBukkitEntity());
    }

    public void processAttack(Item weapon, double strength){
        int damage;
        if (weapon == null){
            damage = 10;
        } else{
            weapon.bakeAttributes();
            damage = (weapon.getBakedAttribute("damage")!=0) ? weapon.getBakedAttribute("damage") : 10;
        }
        this.health = Math.max(health - damage*(1+0.05*strength)/(1+defense*0.05), 0);
        updateEntity();
    }

    public void processAttack(CombatEntityWrapper entity){
        processAttack(entity.weapon, entity.strength);
    }

    public void updateEntity(){
        super.updateEntity();
        if (health <= 0){
            ((LivingEntity)this.source).setHealth(0);
            return;
        }
        ((LivingEntity)this.source).setHealth(this.health/this.maxHealth*20.0);
        PersistentDataManager.setValue(this.source, PersistentDataType.DOUBLE, "health", this.health);
        PersistentDataManager.setValue(this.source, PersistentDataType.DOUBLE, "maxHealth", this.maxHealth);
        PersistentDataManager.setValue(this.source, PersistentDataType.DOUBLE, "defense", this.defense);
    }

    public void updateName(){
        this.source.setCustomName(name + " " + ChatColor.WHITE + ((int)health) + ChatColor.DARK_RED + " ❤");
        this.source.setCustomNameVisible(true);
    }
}
