package io.github.mrriptide.peakcraft.guis;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.InventoryHolder;

public interface InventoryGui extends InventoryHolder {

    boolean onGUIClick(Player player, int slot, InventoryClickEvent e);

    default void onClose(Player player, InventoryCloseEvent e){

    }
}
