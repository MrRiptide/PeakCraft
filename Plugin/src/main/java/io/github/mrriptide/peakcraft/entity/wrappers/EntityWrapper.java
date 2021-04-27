package io.github.mrriptide.peakcraft.entity.wrappers;

import io.github.mrriptide.peakcraft.util.PersistentDataManager;
import net.minecraft.server.v1_16_R3.ChatComponentText;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftCreature;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;

public class EntityWrapper {
    protected String id;
    protected String name;
    protected Entity source;
    public EntityWrapper(Entity creature){
        this.source = creature;
        this.id = PersistentDataManager.getValueOrDefault(creature, PersistentDataType.STRING, "ENTITY_ID", "non_entity");
        this.name = PersistentDataManager.getValueOrDefault(creature, PersistentDataType.STRING, "name", "null");
    }

    public EntityWrapper(CraftCreature creature){
        this(creature.getHandle().getBukkitEntity());
    }

    public void updateEntity(){
        PersistentDataManager.setValue(this.source, PersistentDataType.STRING, "entity_id", this.id);
        this.updateName();
    }

    public void updateName(){
        this.source.setCustomName(name);
        this.source.setCustomNameVisible(true);
    }
}
