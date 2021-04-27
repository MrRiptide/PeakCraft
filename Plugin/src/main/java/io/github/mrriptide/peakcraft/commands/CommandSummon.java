package io.github.mrriptide.peakcraft.commands;

import io.github.mrriptide.peakcraft.entity.BruteEntity;
import io.github.mrriptide.peakcraft.entity.CustomEntity;
import io.github.mrriptide.peakcraft.items.ItemManager;
import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class CommandSummon implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if (!(sender instanceof Player)){
            sender.sendMessage("This command can only be run by a player");
            return false;
        }
        if (args.length == 0 || args.length > 2){
            sender.sendMessage("This command requires only one or two arguments");
            return false;
        }

        int count = (args.length == 2) ? Integer.parseInt(args[1]) : 1;

        Player player = (Player)sender;

        CustomEntity entity = null;

        switch (args[0].toLowerCase()){
            case "brute":
                entity = new BruteEntity(player.getLocation());
                break;
            default:
                sender.sendMessage("I'm sorry but that entity does not exist.");
                return false;
        }

        WorldServer world = ((CraftWorld) player.getWorld()).getHandle();
        entity.applyNBT();
        world.addEntity(entity);

        return true;
    }
}