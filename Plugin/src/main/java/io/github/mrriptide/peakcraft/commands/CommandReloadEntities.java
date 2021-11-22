package io.github.mrriptide.peakcraft.commands;

import io.github.mrriptide.peakcraft.entity.EntityManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class CommandReloadEntities implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        commandSender.sendMessage("Beginning reload of registered entity database.");
        EntityManager.registerEntities();
        commandSender.sendMessage("Reload complete.");
        return true;
    }
}
