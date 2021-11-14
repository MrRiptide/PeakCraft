package io.github.mrriptide.peakcraft.util;

import io.github.mrriptide.peakcraft.entity.LivingEntity;
import io.github.mrriptide.peakcraft.entity.WrapperEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;

public class EntityUtil {
    public static boolean isCustomMob(Entity entity){
        return ((CraftEntity)entity).getHandle() instanceof LivingEntity ||
                PersistentDataManager.getValueOrDefault(entity, PersistentDataType.STRING, "wrappedEntity", "no").equals("yes");
    }

    public static LivingEntity getEntity(Entity entity){
        if (PersistentDataManager.getValueOrDefault(entity, PersistentDataType.STRING, "wrappedEntity", "no").equals("yes")){
            return new WrapperEntity(entity);
        } else if (((CraftEntity)entity).getHandle() instanceof LivingEntity){
            return (LivingEntity) ((CraftEntity)entity).getHandle();
        }
    }
}
