package io.github.mrriptide.peakcraft.entity.data;

import io.github.mrriptide.peakcraft.entity.wrappers.LivingEntityWrapper;
import io.github.mrriptide.peakcraft.exceptions.EntityException;
import io.github.mrriptide.peakcraft.util.PersistentDataManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.SpawnReason;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LivingEntityData {
    protected EntityType model;
    protected String type;
    protected String name;
    protected final String id;
    protected double maxHealth;
    protected double defense;

    public LivingEntityData(Connection conn, ResultSet resultSet) throws SQLException {
        this.model = EntityType.valueOf(resultSet.getString("entity_model"));
        this.type = resultSet.getString("entity_type");
        this.name = resultSet.getString("display_name");
        this.id = resultSet.getString("id");
        this.maxHealth = resultSet.getDouble("max_health");
        this.defense = resultSet.getDouble("defense");
    }

    public void spawn(Location location, CreatureSpawnEvent.SpawnReason reason) throws EntityException {
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(model, name);
        npc.spawn(location, SpawnReason.PLUGIN);
        LivingEntityWrapper wrapper = new LivingEntityWrapper((LivingEntity) npc.getEntity());
        PersistentDataManager.setValue(npc.getEntity(), "mode", "npc");
        wrapper.applyNBT();
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

    public String getType() {
        return type;
    }
}
