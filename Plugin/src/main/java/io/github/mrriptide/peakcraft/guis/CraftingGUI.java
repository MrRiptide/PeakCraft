package io.github.mrriptide.peakcraft.guis;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class CraftingGUI implements InventoryGui {
    private Player player;


    public CraftingGUI(Player player){
        this.player = player;
    }

    @Override
    public Inventory getInventory() {
        // Get the inventory to use as the gui

        Inventory gui = Bukkit.createInventory(this, 45, "Crafting");

        return null;
    }

    @Override
    public boolean onGUIClick(Player player, int slot, InventoryClickEvent e) {
        return false;
    }
}
