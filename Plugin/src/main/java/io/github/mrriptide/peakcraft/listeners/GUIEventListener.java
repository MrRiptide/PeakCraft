package io.github.mrriptide.peakcraft.listeners;

import io.github.mrriptide.peakcraft.guis.InventoryGui;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class GUIEventListener  implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().getHolder() instanceof InventoryGui) {
            InventoryGui gui = (InventoryGui) e.getInventory().getHolder();
            e.setCancelled(gui.onGUIClick((Player)e.getWhoClicked(), e.getRawSlot(), e));
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e){
        if (e.getInventory().getHolder() instanceof InventoryGui) {
            InventoryGui gui = (InventoryGui) e.getInventory().getHolder();
            gui.onClose((Player)e.getPlayer(), e);
        }
    }

}
