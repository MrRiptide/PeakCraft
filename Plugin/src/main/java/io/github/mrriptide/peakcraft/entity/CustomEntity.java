package io.github.mrriptide.peakcraft.entity;

import net.minecraft.server.v1_16_R3.ChatComponentText;
import net.minecraft.server.v1_16_R3.EntityCreature;
import net.minecraft.server.v1_16_R3.EntityTypes;
import net.minecraft.server.v1_16_R3.World;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.scoreboard.ScoreboardManager;

public abstract class CustomEntity extends EntityCreature {
    protected String name;

    protected CustomEntity(EntityTypes<? extends EntityCreature> type, World world) {
        super(type, world);
        updateName();
        setName("Unnamed Mob");
    }

    public void updateName(){
        this.setCustomName(new ChatComponentText(name));
        this.setCustomNameVisible(true);
    }

    public void setName(String name){
        this.name = name;
        updateName();
    }
}
