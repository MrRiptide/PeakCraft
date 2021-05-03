package io.github.mrriptide.peakcraft.entity;

import io.github.mrriptide.peakcraft.items.Item;
import io.github.mrriptide.peakcraft.util.HoloDisplay;
import io.github.mrriptide.peakcraft.util.PersistentDataManager;
import net.minecraft.server.v1_16_R3.ChatComponentText;
import net.minecraft.server.v1_16_R3.EntityCreature;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.World;
import org.bukkit.ChatColor;
import org.bukkit.persistence.PersistentDataType;

public abstract class LivingEntity extends Entity {
    protected double health;
    protected double maxHealth;
    protected double defense;

    protected LivingEntity(EntityTypes<? extends EntityCreature> type, World world) {
        super(type, world);
        updateName();
    }

    @Override
    public void updateName(){
        this.setCustomName(new ChatComponentText(name + " " + ChatColor.WHITE + ((int)health) + ChatColor.DARK_RED + " ❤"));
        this.setCustomNameVisible(true);
    }

    public void processAttack(CombatEntity attacker){
        int damage;

        Item weapon = attacker.getWeapon();

        if (weapon == null){
            damage = 10;
        } else{
            weapon.bakeAttributes();
            damage = (weapon.getBakedAttribute("damage")!=0) ? weapon.getBakedAttribute("damage") : 10;
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
            damageColor = ChatColor.GRAY;
        } else if (multiplier == 1){
            damageColor = ChatColor.WHITE;
        } else {
            damageColor = ChatColor.DARK_RED;
        }
        damageDisplay.showThenDie(damageColor + "" + (int)damagePotential, 40);

        this.health = Math.max(health - damagePotential, 0);
        updateEntity();
    }

    public void updateEntity(){
        super.updateEntity();
        if (health <= 0){
            this.health = 0;
        }
        ((org.bukkit.entity.LivingEntity)this.getBukkitEntity()).setHealth(this.health/this.maxHealth*20.0);
        PersistentDataManager.setValue(this.getBukkitEntity(), PersistentDataType.DOUBLE, "health", this.health);
        PersistentDataManager.setValue(this.getBukkitEntity(), PersistentDataType.DOUBLE, "maxHealth", this.maxHealth);
        PersistentDataManager.setValue(this.getBukkitEntity(), PersistentDataType.DOUBLE, "defense", this.defense);
    }

    public void applyNBT(){
        super.applyNBT();
        PersistentDataManager.setValue(this.getBukkitEntity(), PersistentDataType.DOUBLE, "health", health);
        PersistentDataManager.setValue(this.getBukkitEntity(), PersistentDataType.DOUBLE, "maxHealth", maxHealth);
        PersistentDataManager.setValue(this.getBukkitEntity(), PersistentDataType.DOUBLE, "defense", defense);
    }

    public void setMaxHealth(double maxHealth){
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        updateName();
    }

    public void processAttack(Item weapon, double strength){
        double damage = 1;
        if (weapon != null){
            damage = weapon.getBakedAttribute("damage");
        }

        health -= damage * (1 + strength * 0.05);
        updateName();
    }
}
