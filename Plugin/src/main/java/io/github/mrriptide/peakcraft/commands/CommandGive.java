package io.github.mrriptide.peakcraft.commands;

import io.github.mrriptide.peakcraft.items.Item;
import io.github.mrriptide.peakcraft.items.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CommandGive implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        Player target = Bukkit.getPlayer(args[0]);

        if (target == null){
            sender.sendMessage("Invalid player");
            return false;
        }


        Item item_data;
        ItemStack item;

        try{
            item_data = ItemManager.getItem(args[1]);
        } catch (NullPointerException e){
            sender.sendMessage("Invalid item ID");
            return false;
        }

        if (item_data == null){
            sender.sendMessage("Invalid item ID");
            return false;
        }

        try{
            item = item_data.getItemStack();
        } catch (NullPointerException e){
            sender.sendMessage("Encountered an internal error upon creating the item, check the server logs for more details");
            throw e;
        }

        if (args.length == 3){
            item.setAmount(Integer.parseInt(args[2]));
        }
        target.getInventory().addItem(item);
        return true;
    }
}
