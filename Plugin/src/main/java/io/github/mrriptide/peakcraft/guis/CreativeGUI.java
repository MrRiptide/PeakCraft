package io.github.mrriptide.peakcraft.guis;

import io.github.mrriptide.peakcraft.PeakCraft;
import io.github.mrriptide.peakcraft.exceptions.ItemException;
import io.github.mrriptide.peakcraft.items.ArmorItem;
import io.github.mrriptide.peakcraft.items.Item;
import io.github.mrriptide.peakcraft.items.ItemManager;
import io.github.mrriptide.peakcraft.items.WeaponItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CreativeGUI implements InventoryGui{
    private enum Tab {ITEM, WEAPONS, ARMOR, RELIC}
    private ItemStack[][] armorTabItems;
    private ItemStack[][] weaponTabItems;
    private ItemStack[][] itemTabItems;
    private ItemStack[][] relicTabItems;
    private Tab currentTab;
    private int scroll;

    public CreativeGUI(){
        ArrayList<Item> armorItems = new ArrayList<Item>();
        ArrayList<Item> weaponItems = new ArrayList<Item>();
        ArrayList<Item> itemItems = new ArrayList<Item>();
        ArrayList<Item> relicItems = new ArrayList<Item>();


        for (Item item : ItemManager.getItems().values()){
            if (item instanceof ArmorItem){
                armorItems.add(item);
            } else if (item instanceof WeaponItem){
                weaponItems.add(item);
            } else{
                itemItems.add(item);
            }
            if (item.getRarity() == 7){
                relicItems.add(item);
            }
        }

        armorTabItems = arrayify(armorItems);
        weaponTabItems = arrayify(weaponItems);
        itemTabItems = arrayify(itemItems);
        relicTabItems = arrayify(relicItems);
        scroll = 0;
    }

    private ItemStack[][] arrayify(ArrayList<Item> itemList){
        ItemStack[][] array = new ItemStack[itemList.size() / 7 + 1][7];

        for (int i = 0; i < itemList.size(); i++){
            array[i/7][i % 7] = itemList.get(i).getItemStack();
        }

        return array;
    }

    @Override
    public boolean onGUIClick(Player player, int slot, InventoryClickEvent e) {
        // item tab button
        if (slot == 9){
            scroll = 0;
            loadTab(Tab.ITEM, e.getClickedInventory());
        } else if (slot == 18){
            scroll = 0;
            loadTab(Tab.WEAPONS, e.getClickedInventory());
        } else if (slot == 27){
            scroll = 0;
            loadTab(Tab.ARMOR, e.getClickedInventory());
        } else if (slot == 36){
            scroll = 0;
            loadTab(Tab.RELIC, e.getClickedInventory());
        } else if (slot == 4 && scroll > 0){
            scroll--;
            loadTab(currentTab, e.getClickedInventory());
        } else if (slot == 49 && e.getClickedInventory().getItem(slot).getType() == Material.PLAYER_HEAD){
            scroll++;
            loadTab(currentTab, e.getClickedInventory());
        } else {
            int row = slot / 9;
            int col = slot % 9;
            if (row > 0 && row < 5 && col > 0 && col < 8){
                try{
                    ItemStack item = ItemManager.convertItem(e.getClickedInventory().getItem(slot)).getItemStack();
                    if (e.isShiftClick()){
                        item.setAmount(item.getMaxStackSize());
                    }
                    player.getInventory().addItem(item);
                } catch (ItemException error) {
                    player.sendMessage("You clicked on an invalid item! Please report this!");
                    PeakCraft.getPlugin().getLogger().warning("Player " + player.getName() + " clicked on an invalid item in the creative menu!");
                }

            }
        }
        return slot < 54;
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, 54, "Creative Menu");

        // Create background item
        ItemStack background_item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta background_item_meta = background_item.getItemMeta();
        assert background_item_meta != null;
        background_item_meta.setDisplayName(" ");
        background_item.setItemMeta(background_item_meta);

        for (int i = 0; i < inventory.getSize(); i++){
            inventory.setItem(i, background_item);
        }

        // add item tab
        ItemStack itemTabItem = new ItemStack(Material.DIRT);
        ItemMeta itemMeta = itemTabItem.getItemMeta();
        itemMeta.setDisplayName(ChatColor.WHITE + "Items");
        itemTabItem.setItemMeta(itemMeta);

        inventory.setItem(9, itemTabItem);

        // add weapon tab

        ItemStack weaponTabItem = new ItemStack(Material.NETHERITE_SWORD);
        itemMeta = weaponTabItem.getItemMeta();
        itemMeta.setDisplayName(ChatColor.WHITE + "Weapons");
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        weaponTabItem.setItemMeta(itemMeta);
        inventory.setItem(18, weaponTabItem);

        // add armor tab
        ItemStack armorTabItem = new ItemStack(Material.NETHERITE_CHESTPLATE);
        itemMeta = armorTabItem.getItemMeta();
        itemMeta.setDisplayName(ChatColor.WHITE + "Armor");
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        armorTabItem.setItemMeta(itemMeta);
        inventory.setItem(27, armorTabItem);

        // add relic tab
        ItemStack relicTabItem = new ItemStack(Material.BEACON);
        itemMeta = relicTabItem.getItemMeta();
        itemMeta.setDisplayName(ChatColor.WHITE + "Relics");
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        relicTabItem.setItemMeta(itemMeta);
        inventory.setItem(27, relicTabItem);

        loadTab(Tab.ITEM, inventory);

        return inventory;
    }

    /*private Inventory loadTab(Tab tab, Inventory inventory){
        return loadTab(tab, inventory, 0);
    }*/

    private void loadTab(Tab tab, Inventory inventory){
        this.currentTab = tab;

        ItemStack[][] tabItems = null;
        if (tab == Tab.ITEM){
            tabItems = itemTabItems;
        } else if (tab == Tab.WEAPONS){
            tabItems = weaponTabItems;
        } else if (tab == Tab.ARMOR){
            tabItems = armorTabItems;
        } else if (tab == Tab.RELIC){
            tabItems = relicTabItems;
        }

        for (int i = 0; i < 4; i++){
            for (int j = 0; j < 7; j++){
                assert tabItems != null;
                inventory.setItem(10 + i*9 + j, (i + scroll >= tabItems.length) ? new ItemStack(Material.AIR) : tabItems[i + scroll][j]);
            }
        }

        // Create background item
        ItemStack background_item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta background_item_meta = background_item.getItemMeta();
        assert background_item_meta != null;
        background_item_meta.setDisplayName(" ");
        background_item.setItemMeta(background_item_meta);

        if (scroll > 0){
            ItemStack upArrowItem = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) upArrowItem.getItemMeta();
            meta.setOwningPlayer(Bukkit.getOfflinePlayer("MHF_ArrowUp"));
            meta.setDisplayName(ChatColor.WHITE + "Scroll up (" + scroll + "/" + tabItems.length + ")");
            upArrowItem.setItemMeta(meta);

            inventory.setItem(4, upArrowItem);
        } else {
            inventory.setItem(4, background_item);
        }

        if (scroll + 4 < tabItems.length){
            ItemStack downArrowItem = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta meta = (SkullMeta) downArrowItem.getItemMeta();
            meta.setOwningPlayer(Bukkit.getOfflinePlayer("MHF_ArrowDown"));
            meta.setDisplayName(ChatColor.WHITE + "Scroll down (" + scroll + "/" + tabItems.length + ")");
            downArrowItem.setItemMeta(meta);

            inventory.setItem(49, downArrowItem);
        } else {
            inventory.setItem(49, background_item);
        }
    }
}
