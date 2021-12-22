package io.github.mrriptide.peakcraft.guis;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public interface InventoryGui extends InventoryHolder {

    boolean onGUIClick(Player player, int slot, InventoryClickEvent e);

    default void onClose(Player player, InventoryCloseEvent e){

    }

    default ItemStack getBackgroundItem(){
        ItemStack background_item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta background_item_meta = background_item.getItemMeta();
        assert background_item_meta != null;
        background_item_meta.setDisplayName(" ");
        background_item.setItemMeta(background_item_meta);
        return background_item;
    }

    default void fillInventory(Inventory inventory) {
        // Create background item
        ItemStack background_item = getBackgroundItem();

        for (int i = 0; i < inventory.getSize(); i++){
            inventory.setItem(i, background_item);
        }
    }
}
