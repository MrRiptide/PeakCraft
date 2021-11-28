package io.github.mrriptide.peakcraft.entity.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CombatEntityData extends LivingEntityData {
    protected double strength;

    public CombatEntityData(Connection conn, ResultSet resultSet) throws SQLException {
        super(conn, resultSet);
        PreparedStatement statement = conn.prepareStatement("""
SELECT * FROM combat_entity_data WHERE entity_id = ?;
""");
        statement.setString(1, this.id);
        ResultSet combatResultSet = statement.executeQuery();
        if (combatResultSet.next()){
            this.strength = combatResultSet.getDouble("strength");
        } else {
            this.strength = -1.0;
        }
    }

    public double getStrength() {
        return strength;
    }
}
