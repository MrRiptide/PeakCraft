package io.github.mrriptide.peakcraft.entity;

import io.github.mrriptide.peakcraft.PeakCraft;
import io.github.mrriptide.peakcraft.exceptions.ItemException;
import io.github.mrriptide.peakcraft.util.PersistentDataManager;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class WrapperEntity extends LivingEntity {
    Entity entity;
    CraftEntity bukkitEntity;

    public WrapperEntity(Entity entity) {
        super(entity.getName(),
                (net.minecraft.world.entity.EntityType<? extends PathfinderMob>) ((CraftEntity)entity).getHandle().getType(),
                ((CraftWorld) entity.getWorld()).getHandle());
        this.entity = entity;
    }

    @Override
    public void spawn(Location location, CreatureSpawnEvent.SpawnReason reason){
        updateEntity();
        applyNBT();
        // it should already be spawned???????
    }

    @Override
    public void applyNBT(){
        super.applyNBT();
        PersistentDataManager.setValue(this.getBukkitEntity(), "wrappedEntity", "yes");
    }

    @Override
    public CraftEntity getBukkitEntity(){
        if (bukkitEntity == null){
            if (entity != null)
                bukkitEntity = (CraftEntity) entity;
        }
        return bukkitEntity;
    }

    @Override
    public void initPathfinder() {
        // wrapper entities will not override the default

    }
}
