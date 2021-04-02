package io.github.mrriptide.peakcraft.commands;

import io.github.mrriptide.peakcraft.guis.EditRecipeGUI;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

public class CommandRecipe_Add implements SubCommand {
    @Override
    public boolean onCommand(Player player, Command command, String[] args) {
        if (args.length == 2) {
            EditRecipeGUI gui = new EditRecipeGUI(args[1]);
            player.openInventory(gui.getInventory());
            return true;
        }

        return false;
    }

    @Override
    public String getPermission() {
        return "peakcraft.recipe.add";
    }
}
