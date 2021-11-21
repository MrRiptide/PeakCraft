package io.github.mrriptide.peakcraft.entity.data;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.SpawnReason;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LivingEntityData {
    protected EntityType type;
    protected String name;
    protected final String id;
    protected double maxHealth;
    protected double defense;

    public LivingEntityData(Connection conn, ResultSet resultSet) throws SQLException {
        this.type = EntityType.valueOf(resultSet.getString("entity_type"));
        this.name = resultSet.getString("name");
        this.id = resultSet.getString("id");
        this.maxHealth = resultSet.getDouble("max_health");
        this.maxHealth = resultSet.getDouble("defense");
    }

    public void spawn(Location location, CreatureSpawnEvent.SpawnReason reason){
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(type, name);
        npc.spawn(location, SpawnReason.PLUGIN);
    }

    public EntityType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public double getDefense() {
        return defense;
    }
}
