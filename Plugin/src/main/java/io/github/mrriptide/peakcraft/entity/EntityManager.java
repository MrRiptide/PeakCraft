package io.github.mrriptide.peakcraft.entity;

import io.github.mrriptide.peakcraft.exceptions.EntityException;
import org.bukkit.Location;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public abstract class EntityManager {
    private static HashMap<String, Class<? extends LivingEntity>> entities;

    public static void registerEntities(){
        entities = new HashMap<>();
        entities.put("brute", BruteEntity.class);
        entities.put("potato_king", PotatoKingEntity.class);
    }

    public static LivingEntity getEntity(String id, Location location) throws EntityException {
        if (entities == null){
            registerEntities();
        }
        if (entities.containsKey(id)){
            try {
                Constructor<? extends LivingEntity> constructor = entities.get(id).getConstructor(Location.class);
                return constructor.newInstance(location);
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                e.printStackTrace();
                throw new EntityException("An entity called \"" + id + "\" was found but could not be instantiated");
            }
        } else {
            throw new EntityException("No valid entity called \"" + id + "\" exists");
        }
    }
}
