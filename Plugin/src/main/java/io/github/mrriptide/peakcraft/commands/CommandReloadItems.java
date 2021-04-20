package io.github.mrriptide.peakcraft.commands;

import io.github.mrriptide.peakcraft.items.ItemManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandReloadItems implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        ItemManager.loadItems();
        sender.sendMessage("Reloaded items from file.");
        return true;
    }
}
