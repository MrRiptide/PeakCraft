package io.github.mrriptide.peakcraft.util;

import io.github.mrriptide.peakcraft.PeakCraft;
import io.github.mrriptide.peakcraft.entity.CustomEntity;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.persistence.PersistentDataType;

public class HoloDisplay {
    private Location location;
    public HoloDisplay(Location loc){
        this.location = loc;
    }

    public void showThenDie(String displayText, int ticks){
        ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        armorStand.setVisible(false);
        armorStand.setInvulnerable(true);
        armorStand.setGravity(false);
        armorStand.setCustomName(displayText);
        armorStand.setCustomNameVisible(true);

        Bukkit.getScheduler().runTaskLater(PeakCraft.instance, armorStand::remove, ticks);
    }
}
