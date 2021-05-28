package io.github.mrriptide.peakcraft.commands;

import io.github.mrriptide.peakcraft.util.CustomColors;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

public class CommandRecipe implements CommandExecutor {

    private Map<String, SubCommand> subCommands = new HashMap<>();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            if (!subCommands.containsKey(args[0].toLowerCase())) {
                player.sendMessage("Invalid subcommand. Valid subcommands are: " + subCommands.keySet().toString());
                return false;
            }

            SubCommand subCommand = subCommands.get(args[0]);
            if (player.hasPermission(subCommand.getPermission())){
                return subCommand.onCommand(player, command, args);
            } else {
                player.sendMessage(CustomColors.ERROR + "You do not have permission to use this command.");
                return false;
            }
        } else {
            sender.sendMessage("This command can only be used by a player");
            return false;
        }
    }

    public void registerCommand(String cmd, SubCommand subCommand) {
        subCommands.put(cmd, subCommand);
    }
}
