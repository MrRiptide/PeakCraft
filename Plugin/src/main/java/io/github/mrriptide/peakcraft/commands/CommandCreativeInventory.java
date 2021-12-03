package io.github.mrriptide.peakcraft.commands;

import io.github.mrriptide.peakcraft.guis.CreativeGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandCreativeInventory implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player){
            ((Player)sender).openInventory((new CreativeGUI()).getInventory());
            return true;
        } else {
            sender.sendMessage("This command can only be run by a player!");
            return false;
        }
    }
}
