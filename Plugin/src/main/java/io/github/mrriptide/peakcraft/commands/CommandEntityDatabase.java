package io.github.mrriptide.peakcraft.commands;

import io.github.mrriptide.peakcraft.util.Formatter;
import io.github.mrriptide.peakcraft.util.MySQLHelper;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.*;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CommandEntityDatabase implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player)){
            commandSender.sendMessage("This command can only be run by a player");
            return false;
        }
        World world = ((Player) commandSender).getWorld();
        Location location = ((Player) commandSender).getLocation();
        commandSender.sendMessage("Starting the entries of entities");
        try {
            Connection connection = MySQLHelper.getConnection();

            for (EntityType entityType : EntityType.values()){
                Entity spawnedEntity = world.spawnEntity(location, entityType);
                if (!(spawnedEntity instanceof LivingEntity entity)){
                    spawnedEntity.remove();
                    continue;
                }
                PreparedStatement statement = connection.prepareStatement("""
INSERT INTO entity_data VALUES (?, ?, ?, ?, ?, ?)
""");
                /*id varchar(255) NOT NULL,
max_health double NOT NULL,
defense double NOT NULL,
display_name varchar(255) NOT NULL,
entity_type varchar(255) NOT NULL,

                * */
                String type = "living";
                if (entity instanceof Monster){
                    type = "combat";
                    PreparedStatement combat_statement = connection.prepareStatement("""
INSERT INTO combat_entity_data values (?, ?, ?);
""");
                    combat_statement.setString(1, entityType.name());
                    combat_statement.setDouble(2, entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue());
                    combat_statement.setDouble(3, entity.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK).getValue());
                    combat_statement.execute();
                    combat_statement.close();
                }
                statement.setString(1, type);
                statement.setString(2, entityType.name());
                statement.setDouble(3, entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
                statement.setDouble(4, entity.getAttribute(Attribute.GENERIC_ARMOR).getValue());
                statement.setString(5, Formatter.humanize(entityType.name()));
                statement.setString(6, entityType.name());

                statement.execute();
                statement.close();
                if (type.equals("combat")){
                    PreparedStatement combat_statement = connection.prepareStatement("""
INSERT INTO combat_entity_data values (?, ?, ?);
""");
                    combat_statement.setString(1, entityType.name());
                    combat_statement.setDouble(2, entity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getValue());
                    combat_statement.setDouble(3, entity.getAttribute(Attribute.GENERIC_ATTACK_KNOCKBACK).getValue());
                    combat_statement.execute();
                    combat_statement.close();
                }

                spawnedEntity.remove();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        commandSender.sendMessage("Successfully created entries for all of the entities");
        return true;
    }
}
