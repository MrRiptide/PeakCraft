package io.github.mrriptide.peakcraft.entity;

import io.github.mrriptide.peakcraft.util.CustomColors;
import net.minecraft.world.entity.EntityType;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;

public class BruteEntity extends HostileEntity {
    public BruteEntity(Location loc){
        super("brute", EntityType.ZOMBIE, ((CraftWorld) loc.getWorld()).getHandle());
        this.setPos(loc.getX(), loc.getY(), loc.getZ());
        setName(CustomColors.BASIC_ENTITY +"Brute");
        setMaxHealth(10000);
        this.strength = 50;
    }
}
