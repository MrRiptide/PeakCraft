package io.github.mrriptide.peakcraft.commands;

import io.github.mrriptide.peakcraft.items.EnchantableItem;
import io.github.mrriptide.peakcraft.items.WeaponItem;
import io.github.mrriptide.peakcraft.items.enchantments.Enchantment;
import io.github.mrriptide.peakcraft.items.Item;
import io.github.mrriptide.peakcraft.items.enchantments.EnchantmentManager;
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

        if (args.length != 3){
            sender.sendMessage("Please provide 3 arguments");
            return false;
        }

        String enchantName = args[1];

        if (!EnchantmentManager.validateEnchantment(enchantName)){
            sender.sendMessage("Invalid enchant name");
            return false;
        }

        int enchantLevel = Integer.parseInt(args[2]);

        if (enchantLevel < 0){
            sender.sendMessage("Cannot provide an enchantment level below 0");
            return false;
        } else if (enchantLevel == 0){
            sender.sendMessage("Removed enchantment");

            EnchantableItem newItem = new EnchantableItem(enchantPlayer.getInventory().getItemInMainHand());
            newItem.removeEnchantment(enchantName);
            enchantPlayer.getInventory().setItemInMainHand(newItem.getItemStack());
            return true;
        } else {
            EnchantableItem newItem = new EnchantableItem(enchantPlayer.getInventory().getItemInMainHand());
            newItem.addEnchantment(enchantName, enchantLevel);
            enchantPlayer.getInventory().setItemInMainHand(newItem.getItemStack());
            return true;
        }
    }
}
