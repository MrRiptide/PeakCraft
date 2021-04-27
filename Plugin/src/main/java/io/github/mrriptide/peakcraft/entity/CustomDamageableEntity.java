package io.github.mrriptide.peakcraft.entity;

import io.github.mrriptide.peakcraft.items.Item;
import io.github.mrriptide.peakcraft.util.PersistentDataManager;
import net.minecraft.server.v1_16_R3.ChatComponentText;
import net.minecraft.server.v1_16_R3.EntityCreature;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.World;
import org.bukkit.ChatColor;
import org.bukkit.persistence.PersistentDataType;

public abstract class CustomDamageableEntity extends CustomEntity {
    protected double health;
    protected double maxHealth;
    protected double defense;

    protected CustomDamageableEntity(EntityTypes<? extends EntityCreature> type, World world) {
        super(type, world);
        updateName();
    }

    @Override
    public void updateName(){
        this.setCustomName(new ChatComponentText(name + " " + ChatColor.WHITE + ((int)health) + ChatColor.DARK_RED + " ❤"));
        this.setCustomNameVisible(true);
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
