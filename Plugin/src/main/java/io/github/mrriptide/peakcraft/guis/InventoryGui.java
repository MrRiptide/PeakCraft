package io.github.mrriptide.peakcraft.guis;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public interface InventoryGui extends InventoryHolder {

    boolean onGUIClick(Player player, int slot, InventoryClickEvent e);
}
