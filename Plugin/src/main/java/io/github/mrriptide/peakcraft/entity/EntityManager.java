package io.github.mrriptide.peakcraft.entity;

import io.github.mrriptide.peakcraft.PeakCraft;
import io.github.mrriptide.peakcraft.entity.data.CombatEntityData;
import io.github.mrriptide.peakcraft.entity.data.LivingEntityData;
import io.github.mrriptide.peakcraft.entity.wrappers.LivingEntityWrapper;
import io.github.mrriptide.peakcraft.exceptions.EntityException;
import io.github.mrriptide.peakcraft.exceptions.NoSuchEntityException;
import io.github.mrriptide.peakcraft.util.MySQLHelper;
import io.github.mrriptide.peakcraft.util.PersistentDataManager;
import io.github.mrriptide.peakcraft.util.WeightedRandom;
import net.citizensnpcs.api.CitizensAPI;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.persistence.PersistentDataType;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class EntityManager {
    private static HashMap<String, LivingEntityData> entities;

    public static void registerEntities(){
        entities = new HashMap<>();

        try {
            Connection conn = MySQLHelper.getConnection();
            PreparedStatement statement = conn.prepareStatement("""
SELECT * FROM entity_data;
""");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()){
                if ("combat".equals(resultSet.getString("type"))) {
                    entities.put(resultSet.getString("id"), new CombatEntityData(conn, resultSet));
                } else {
                    entities.put(resultSet.getString("id"), new LivingEntityData(conn, resultSet));
                }
            }
        } catch (SQLException e) {
            PeakCraft.getPlugin().getLogger().warning("Registering entities failed. Error follows");
            e.printStackTrace();
            PeakCraft.disable();
        }
    }

    // okay so i want to let the code spawn an entity from id, which may be very difficult tbh

    public static boolean convertSpawn(Entity entity, Location location, boolean dynamicSelect, CreatureSpawnEvent.SpawnReason reason) throws EntityException {
        WeightedRandom<LivingEntityData> entityChoices = new WeightedRandom<>();
        try {
            Connection conn = MySQLHelper.getConnection();
            PreparedStatement statement = conn.prepareStatement("""
SELECT * FROM entity_conversion_choices WHERE entity_type = ?;
""");
            statement.setString(1, entity.getType().name());

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()){
                String entity_id = resultSet.getString("entity_id");
                if (resultSet.wasNull())
                    entityChoices.add(null, resultSet.getInt("weight"));
                else
                    entityChoices.add(getEntity(entity_id), resultSet.getInt("weight"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        LivingEntityData entityData = entityChoices.getRandom();

        if (entityData == null){
            LivingEntityWrapper entityWrapper = new LivingEntityWrapper((LivingEntity) entity);
            entityWrapper.applyNBT();
            PersistentDataManager.setValue(entityWrapper.getEntity(), "mode", "wrapped");
            return false;
        } else {
            entityData.spawn(location, reason);
            return true;
        }
    }

    // okay so i want to let the code spawn an entity from id, which may be very difficult tbh
    public static void spawnEntity(String id, Location location, CreatureSpawnEvent.SpawnReason reason) throws EntityException {
        spawnEntity(id, location, reason, false);
    }

    public static void spawnEntity(String id, Location location, CreatureSpawnEvent.SpawnReason reason, boolean dynamicSelect) throws EntityException {
        spawnEntity(getEntity(id), location, reason);
    }

    public static void spawnEntity(LivingEntityData entity, Location location, CreatureSpawnEvent.SpawnReason reason) throws EntityException{
        entity.spawn(location, reason);
    }

    public static LivingEntityData getEntity(String id) throws EntityException {
        if (entities == null){
            registerEntities();
        }
        if (entities.containsKey(id.toUpperCase())){
            return entities.get(id.toUpperCase());
        } else {
            throw new NoSuchEntityException("No valid entity called \"" + id + "\" exists");
        }
    }

    // spawns custom entities
    public static LivingEntity getEntity(Class<? extends LivingEntity> entityClass, Location location) throws EntityException {
        try {
            Constructor<? extends LivingEntity> constructor = entityClass.getConstructor(Location.class);
            return constructor.newInstance(location);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
            throw new EntityException("An entity called \"" + entityClass.getName() + "\" was found but could not be instantiated");
        }
    }

    public static boolean isCustomMob(Entity entity){
        return isWrapped(entity) || isNPC(entity);
    }

    public static boolean isPlayer(Entity entity){
        return entity instanceof Player && !CitizensAPI.getNPCRegistry().isNPC(entity);
    }

    public static boolean isNPC(Entity entity){
        return entity.hasMetadata("NPC");
    }

    public static boolean isWrapped(Entity entity){
        return !PersistentDataManager.getValueOrDefault(entity, PersistentDataType.STRING, "mode", "none").equals("none");
    }
}
