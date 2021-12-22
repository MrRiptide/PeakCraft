package io.github.mrriptide.peakcraft.commands;

import io.github.mrriptide.peakcraft.entity.player.PlayerManager;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandClearAttributes implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player){
            for (NamespacedKey key : ((Player)commandSender).getPersistentDataContainer().getKeys()){
                if (key.getNamespace().equalsIgnoreCase("peakcraft")){
                    ((Player)commandSender).getPersistentDataContainer().remove(key);
                }
            }
            commandSender.sendMessage("Cleared all attributes!");
            PlayerManager.logOutPlayer((Player) commandSender);
            PlayerManager.logInPlayer((Player) commandSender);
            return true;
        } else {
            commandSender.sendMessage("This command can only be run by a player");
            return false;
        }
    }
}
