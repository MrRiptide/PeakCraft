package io.github.mrriptide.peakcraft.commands;

import io.github.mrriptide.peakcraft.entity.EntityManager;
import io.github.mrriptide.peakcraft.entity.LivingEntity;
import io.github.mrriptide.peakcraft.exceptions.EntityException;
import net.minecraft.server.level.ServerLevel;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.Locale;

public class CommandSummon implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if (!(sender instanceof Player)){
            sender.sendMessage("This command can only be run by a player");
            return false;
        }
        if (args.length == 0 || args.length > 3){
            sender.sendMessage("This command requires only one or two arguments");
            return false;
        }

        int count = (args.length == 2) ? Integer.parseInt(args[1]) : 1;
        boolean dynamicSelect = args.length == 3 && Boolean.parseBoolean(args[2]);

        Player player = (Player)sender;

        LivingEntity entity = null;

        try {
            entity = EntityManager.spawnEntity(args[0].toLowerCase(Locale.ROOT), player.getLocation(), CreatureSpawnEvent.SpawnReason.COMMAND, dynamicSelect);
        } catch (EntityException e) {
            e.printStackTrace();
            sender.sendMessage("I'm sorry but that entity does not exist.");
            return false;
        }

        entity.applyNBT();

        return true;
    }
}