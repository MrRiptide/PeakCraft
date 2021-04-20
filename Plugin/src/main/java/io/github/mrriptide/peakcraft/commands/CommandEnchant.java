package io.github.mrriptide.peakcraft.commands;

import io.github.mrriptide.peakcraft.Enchantment;
import io.github.mrriptide.peakcraft.Item;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandEnchant implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player enchantPlayer = Bukkit.getPlayer(args[0]);
        if (enchantPlayer == null){
            return false;
        }

        String enchantName = args[1];
        int enchantLevel = Integer.parseInt(args[2]);

        if (enchantLevel < 0){
            sender.sendMessage("Cannot provide an enchantment level below 0");
            return false;
        } else if (enchantLevel == 0){
            sender.sendMessage("Removed enchantment");

            Item newItem = new Item(enchantPlayer.getInventory().getItemInMainHand());
            newItem.removeEnchantment(new Enchantment(enchantName, enchantLevel));
            enchantPlayer.getInventory().setItemInMainHand(newItem.getItemStack());
            return true;
        } else {
            Item newItem = new Item(enchantPlayer.getInventory().getItemInMainHand());
            newItem.addEnchantment(new Enchantment(enchantName, enchantLevel));
            enchantPlayer.getInventory().setItemInMainHand(newItem.getItemStack());
            return true;
        }
    }
}
