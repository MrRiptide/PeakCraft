package io.github.mrriptide.peakcraft.entity;

import net.minecraft.server.v1_16_R3.EntityTypes;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;

public class BruteEntity extends CombatEntity {
    public BruteEntity(Location loc){
        super(EntityTypes.ZOMBIE, ((CraftWorld) loc.getWorld()).getHandle());
        this.setPosition(loc.getX(), loc.getY(), loc.getZ());
        setName(ChatColor.GREEN +"Brute");
        setMaxHealth(100);
        this.strength = 50;
    }
}
