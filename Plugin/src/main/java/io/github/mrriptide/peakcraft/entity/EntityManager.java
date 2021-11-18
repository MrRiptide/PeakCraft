package io.github.mrriptide.peakcraft.entity;

import io.github.mrriptide.peakcraft.exceptions.EntityException;
import io.github.mrriptide.peakcraft.exceptions.NoSuchEntityException;
import io.github.mrriptide.peakcraft.util.PersistentDataManager;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public abstract class EntityManager {
    private static HashMap<String, Class<? extends LivingEntity>> entities;

    public static void registerEntities(){
        entities = new HashMap<>();
        entities.put("BRUTE", BruteEntity.class);
        entities.put("POTATO_KING", PotatoKingEntity.class);
    }

    // okay so i want to let the code spawn an entity from id, which may be very difficult tbh
    public static LivingEntity spawnEntity(String id, Location location, CreatureSpawnEvent.SpawnReason reason) throws EntityException {
        return spawnEntity(id, location, reason, false);
    }

    public static LivingEntity spawnEntity(String id, Location location, CreatureSpawnEvent.SpawnReason reason, boolean dynamicSelect) throws EntityException {
        return spawnEntity(getEntity(id, location, dynamicSelect), location, reason);
    }

    public static LivingEntity spawnEntity(LivingEntity entity, Location location, CreatureSpawnEvent.SpawnReason reason) throws EntityException{
        entity.spawn(location, reason);
        return entity;
    }

    // this should probably only be used internally, right?
    private static LivingEntity getEntity(String id, Location location) throws EntityException {
        return getEntity(id, location, false);
    }

    private static LivingEntity getEntity(String id, Location location, boolean dynamicSelect) throws EntityException {
        if (entities == null){
            registerEntities();
        }
        if (entities.containsKey(id.toUpperCase())){
            return getLivingEntity(entities.get(id.toUpperCase()), location);
        } else if (EnumUtils.isValidEnum(EntityType.class, id.toUpperCase())) {
            return getEntity(EntityType.valueOf(id.toUpperCase()), location, dynamicSelect);
        } else {
            throw new NoSuchEntityException("No valid entity called \"" + id + "\" exists");
        }
    }

    // spawns custom entities
    public static LivingEntity getLivingEntity(Class<? extends LivingEntity> entityClass, Location location) throws EntityException {
        try {
            Constructor<? extends LivingEntity> constructor = entityClass.getConstructor(Location.class);
            return constructor.newInstance(location);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            throw new EntityException("An entity called \"" + entityClass.getName() + "\" was found but could not be instantiated");
        }
    }

    public static LivingEntity getCustomEntityFromEntity(Entity entity) throws EntityException {
        if (PersistentDataManager.getValueOrDefault(entity, PersistentDataType.STRING, "wrappedEntity", "no").equals("yes")){
            return new WrapperEntity(entity);
        } else if (((CraftEntity)entity).getHandle() instanceof LivingEntity){
            return (LivingEntity) ((CraftEntity)entity).getHandle();
        } else {
            throw new EntityException("Invalid entity");
        }
    }

    public static LivingEntity getEntity(Entity entity, Location location, boolean dynamicSelect){
        if (dynamicSelect){
            LivingEntity createdEntity = getEntity(entity.getClass(), location);
            if (createdEntity != null){
                return createdEntity;
            }
        }

        return new WrapperEntity(entity);
    }

    public static LivingEntity getEntity(EntityType entityType, Location location, boolean dynamicSelect){
        if (dynamicSelect){
            LivingEntity createdEntity = getEntity(entityType.getEntityClass(), location);
            if (createdEntity != null){
                return createdEntity;
            }
        }

        return new WrapperEntity(entityType, location);
    }

    public static LivingEntity getEntity(Class<? extends Entity> entityClass, Location location){
        if (entityClass.equals(Zombie.class)){
            return new BruteEntity(location);
        }

        return null;
    }

    public static boolean isCustomMob(Entity entity){
        return isFullCustomMob(entity) || isWrapped(entity);
    }

    public static boolean isWrapped(Entity entity){
        return PersistentDataManager.getValueOrDefault(entity, PersistentDataType.STRING, "wrappedEntity", "no").equals("yes");
    }

    public static boolean isFullCustomMob(Entity entity){
        return ((CraftEntity)entity).getHandle() instanceof LivingEntity;
    }
}
