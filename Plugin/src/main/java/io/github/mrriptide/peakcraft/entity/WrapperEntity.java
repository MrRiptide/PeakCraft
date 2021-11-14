package io.github.mrriptide.peakcraft.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.Level;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;

public class WrapperEntity extends LivingEntity {
    Entity entity;

    public WrapperEntity(Entity entity) {
        super(entity.getName(),
                (EntityType<? extends PathfinderMob>) ((CraftEntity)entity).getHandle().getType(),
                ((CraftWorld) entity.getWorld()).getHandle());
        this.entity = entity;
    }

    @Override
    public CraftEntity getBukkitEntity(){
        return (CraftEntity) entity;
    }

    @Override
    public void initPathfinder() {
        // wrapper entities will not override the default

    }
}
