package io.github.mrriptide.peakcraft.commands;

import io.github.mrriptide.peakcraft.entity.player.PlayerManager;
import io.github.mrriptide.peakcraft.entity.player.PlayerWrapper;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandBalance implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player){
            PlayerWrapper player = PlayerManager.getPlayer((Player)commandSender);
            commandSender.sendMessage("Your current balance is : " + player.getCoins());
            return true;
        } else {
            commandSender.sendMessage("This command can only be run by a player!");
            return false;
        }
    }
}
