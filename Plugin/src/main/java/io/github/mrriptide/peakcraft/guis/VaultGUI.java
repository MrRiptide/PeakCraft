package io.github.mrriptide.peakcraft.guis;

import io.github.mrriptide.peakcraft.entity.player.PlayerManager;
import io.github.mrriptide.peakcraft.entity.player.PlayerWrapper;
import io.github.mrriptide.peakcraft.items.Item;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

public class VaultGUI implements InventoryGui{
    PlayerWrapper player;
    Item[][] items;

    public VaultGUI(Player player){
        this.player = PlayerManager.getPlayer(player);
    }

    @Override
    public @NotNull Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, 54, player.getName() + "'s Vault");
        loadPage(0, inventory);
        return inventory;
    }

    @Override
    public boolean onGUIClick(Player player, int slot, InventoryClickEvent e) {
        if (slot >= 44 && slot <= 54){
            return true;
        } else {
            return false;
        }
    }

    private void loadPage(int page, Inventory inventory){
        // Create background item
        ItemStack background_item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta background_item_meta = background_item.getItemMeta();
        assert background_item_meta != null;
        background_item_meta.setDisplayName(" ");
        background_item.setItemMeta(background_item_meta);
        for (int i = 46; i < 53; i++){
            inventory.setItem(i, background_item);
        }

        if (page > 0){
            ItemStack leftArrowItem = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) leftArrowItem.getItemMeta();
            meta.setOwningPlayer(Bukkit.getOfflinePlayer("MHF_ArrowLeft"));
            meta.setDisplayName(ChatColor.WHITE + "Page left (" + page+1 + "/" + items.length + ")");
            leftArrowItem.setItemMeta(meta);

            inventory.setItem(45, leftArrowItem);
        }
        if (page < items.length-1){
            ItemStack rightArrowItem = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) rightArrowItem.getItemMeta();
            meta.setOwningPlayer(Bukkit.getOfflinePlayer("MHF_ArrowRight"));
            meta.setDisplayName(ChatColor.WHITE + "Page right (" + page+1 + "/" + items.length + ")");
            rightArrowItem.setItemMeta(meta);

            inventory.setItem(45, rightArrowItem);
        }
    }
}
