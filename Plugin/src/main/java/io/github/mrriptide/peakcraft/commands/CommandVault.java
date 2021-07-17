package io.github.mrriptide.peakcraft.commands;

import io.github.mrriptide.peakcraft.guis.CreativeGUI;
import io.github.mrriptide.peakcraft.guis.VaultGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CommandVault implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player){
            VaultGUI vaultGUI = new VaultGUI((Player)commandSender);
            if (!vaultGUI.failedLoading()){
                ((Player)commandSender).openInventory(vaultGUI.getInventory());
                return true;
            } else {
                return false;
            }
        } else {
            commandSender.sendMessage("This command can only be run by a player!");
            return false;
        }
    }
}
