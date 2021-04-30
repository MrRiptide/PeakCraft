package io.github.mrriptide.peakcraft.util;

import io.github.mrriptide.peakcraft.PeakCraft;
import net.minecraft.server.v1_16_R3.ChatComponentText;
import net.minecraft.server.v1_16_R3.EntityArmorStand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;

public class HoloDisplay {
    private Location location;
    public HoloDisplay(Location loc){
        this.location = loc;
    }

    public void showThenDie(String displayText, int ticks){
        EntityArmorStand entityArmorStand = new EntityArmorStand(((CraftWorld) location.getWorld()).getHandle(), location.getX(), location.getY(), location.getZ());

        entityArmorStand.setInvisible(true);
        entityArmorStand.setInvulnerable(true);
        entityArmorStand.setNoGravity(true);
        entityArmorStand.setCustomName(new ChatComponentText(displayText));
        entityArmorStand.setCustomNameVisible(true);

        ((CraftWorld) location.getWorld()).getHandle().addEntity(entityArmorStand);
        Bukkit.getScheduler().runTaskLater(PeakCraft.instance,
                () -> ((CraftWorld) location.getWorld()).getHandle().removeEntity(entityArmorStand), ticks);
    }
}
