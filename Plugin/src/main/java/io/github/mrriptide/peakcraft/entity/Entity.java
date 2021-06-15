package io.github.mrriptide.peakcraft.entity;

import io.github.mrriptide.peakcraft.util.PersistentDataManager;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.ScoreboardManager;

public abstract class Entity extends EntityCreature {
    protected String name;
    protected final String id;

    protected Entity(String id, EntityTypes<? extends EntityCreature> type, World world) {
        super(type, world);
        updateName();
        setName("Unnamed Mob");
        this.id = id;
    }

    public void updateEntity(){
        this.updateName();
    }

    public void updateName(){
        this.setCustomName(new ChatComponentText(name));
        this.setCustomNameVisible(true);
    }

    public void setName(String name){
        this.name = name;

        updateName();
    }

    public void applyNBT(){
        PersistentDataManager.setValue(this.getBukkitEntity(), PersistentDataType.STRING, "name", name);
        PersistentDataManager.setValue(this.getBukkitEntity(), PersistentDataType.STRING, "id", id);
    }
}
