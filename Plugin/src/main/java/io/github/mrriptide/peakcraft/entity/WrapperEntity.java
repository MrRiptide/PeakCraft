package io.github.mrriptide.peakcraft.entity;

import net.minecraft.world.entity.PathfinderMob;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.lang.reflect.InvocationTargetException;

public class WrapperEntity extends LivingEntity {
    Entity entity;
    CraftEntity bukkitEntity;
    EntityType entityType;

    public WrapperEntity(Entity entity) {
        super(entity.getName(),
                (net.minecraft.world.entity.EntityType<? extends PathfinderMob>) ((CraftEntity)entity).getHandle().getType(),
                ((CraftWorld) entity.getWorld()).getHandle());
        this.entity = entity;
    }

    public WrapperEntity(EntityType entityType, Location location){
        super(entityType.name(),
                (net.minecraft.world.entity.EntityType<? extends PathfinderMob>)
                        net.minecraft.world.entity.EntityType.byString(entityType.name()).get(),
                ((CraftWorld) location.getWorld()).getHandle());
        this.entityType = entityType;
    }

    @Override
    public void spawn(Location location, CreatureSpawnEvent.SpawnReason reason){
        ((CraftWorld)location.getWorld()).addEntity(getBukkitEntity().getHandle(), reason);

        updateEntity();
        applyNBT();
    }

    @Override
    public CraftEntity getBukkitEntity(){
        if (bukkitEntity == null){
            if (entity != null)
                bukkitEntity = (CraftEntity) entity;
            else if (entityType != null){
                net.minecraft.world.entity.EntityType type =
                        net.minecraft.world.entity.EntityType.byString(entityType.name()).get();
                try {
                    bukkitEntity =
                            (CraftEntity) type.getBaseClass().getDeclaredConstructor().newInstance();
                } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return bukkitEntity;
    }

    @Override
    public void initPathfinder() {
        // wrapper entities will not override the default

    }
}
