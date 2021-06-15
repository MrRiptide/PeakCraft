package io.github.mrriptide.peakcraft.entity;

import io.github.mrriptide.peakcraft.util.CustomColors;
import net.minecraft.server.v1_16_R3.EntityTypes;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;

public class BruteEntity extends HostileEntity {
    public BruteEntity(Location loc){
        super("brute", EntityTypes.ZOMBIE, ((CraftWorld) loc.getWorld()).getHandle());
        this.setPosition(loc.getX(), loc.getY(), loc.getZ());
        setName(CustomColors.BASIC_ENTITY +"Brute");
        setMaxHealth(10000);
        this.strength = 50;
    }
}
