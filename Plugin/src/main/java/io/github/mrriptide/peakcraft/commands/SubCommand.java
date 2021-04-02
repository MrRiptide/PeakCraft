package io.github.mrriptide.peakcraft.commands;

import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public interface SubCommand {
    boolean onCommand(Player player, Command command, String[] args);
    String getPermission();
}
