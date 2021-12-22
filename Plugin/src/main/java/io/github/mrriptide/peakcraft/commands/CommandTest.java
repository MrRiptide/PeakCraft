package io.github.mrriptide.peakcraft.commands;

import io.github.mrriptide.peakcraft.PeakCraft;
import io.github.mrriptide.peakcraft.util.MySQLHelper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class CommandTest implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        String[] materials = {"LEATHER", "WOODEN", "IRON", "CHAINMAIL", "GOLDEN", "DIAMOND", "NETHERITE"};
        String[] item_types = {"SWORD", "PICKAXE", "AXE", "SHOVEL", "HOE", "HELMET", "CHESTPLATE", "LEGGINGS", "BOOTS"};
        // prot 5 will cost 15 points ( 3*level )
        // special prot 5 will cost 10 points ( 2*level)
        // thorns 5 will cost 15 points ( 3*level )
        // frost/depth/soulspeed 3 will cost 3 points ( 1*level )
        int[] material_multipliers = {2, 2, 3, 5, 4, 8, 10};
        double[] type_multipliers = {3, 1, 1, 1, 1, 3, 3, 3, 3.5};

        try {
            Connection conn = MySQLHelper.getConnection();

            for (int i = 0; i < materials.length; i++){
                for (int j = 0; j < item_types.length; j++){
                    // if leather/chainmail and a tool type
                    if ((i == 0 || i == 3) && j < 5){
                        continue;
                    }
                    // if wood and an armor type
                    if (i == 1 && j > 4){
                        continue;
                    }
                    PeakCraft.getPlugin().getLogger().info("Item \"" + materials[i] + "_" + item_types[j] + " gets a max enchants value of "
                            + (int)(material_multipliers[i]*type_multipliers[j]) + " points.");

                    // CHANGE THE COSTS
                    PreparedStatement statement = conn.prepareStatement("""
INSERT INTO item_attributes (item_id, attribute_id, value) VALUES(?,"max_enchantment_points",?);
""");
                    statement.setString(1, materials[i] + "_" + item_types[j]);
                    statement.setInt(2, (int)(material_multipliers[i]*type_multipliers[j]));

                    statement.execute();
                    statement.close();
                }
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }



        commandSender.sendMessage("Done!");
        return true;
    }
}
