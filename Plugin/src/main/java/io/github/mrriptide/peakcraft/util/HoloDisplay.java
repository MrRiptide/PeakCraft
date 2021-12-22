package io.github.mrriptide.peakcraft.util;

import io.github.mrriptide.peakcraft.PeakCraft;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class HoloDisplay {
    private Location location;
    public HoloDisplay(Location loc){
        this.location = loc;
    }

    public void showThenDie(String displayText, int ticks){
        ArmorStand entityArmorStand = new ArmorStand(((CraftWorld) location.getWorld()).getHandle(), location.getX(), location.getY(), location.getZ());

        entityArmorStand.setInvisible(true);
        entityArmorStand.setInvulnerable(true);
        entityArmorStand.setNoGravity(true);
        entityArmorStand.setCustomName(new TextComponent(displayText));
        entityArmorStand.setCustomNameVisible(true);

        ((CraftWorld) location.getWorld()).getHandle().addFreshEntity(entityArmorStand, CreatureSpawnEvent.SpawnReason.CUSTOM);
        Bukkit.getScheduler().runTaskLater(PeakCraft.instance,
                () -> entityArmorStand.remove(Entity.RemovalReason.KILLED), ticks);
    }
}
