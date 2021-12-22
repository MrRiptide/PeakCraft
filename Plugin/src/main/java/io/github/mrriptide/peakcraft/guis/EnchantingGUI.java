package io.github.mrriptide.peakcraft.guis;

import io.github.mrriptide.peakcraft.PeakCraft;
import io.github.mrriptide.peakcraft.exceptions.ItemException;
import io.github.mrriptide.peakcraft.items.EnchantableItem;
import io.github.mrriptide.peakcraft.items.ItemManager;
import io.github.mrriptide.peakcraft.items.enchantments.EnchantmentData;
import io.github.mrriptide.peakcraft.items.enchantments.EnchantmentManager;
import io.github.mrriptide.peakcraft.recipes.CustomItemStack;
import io.github.mrriptide.peakcraft.util.CustomColors;
import io.github.mrriptide.peakcraft.util.PersistentDataManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EnchantingGUI implements InventoryGui {
    EnchantmentData currentData = null;

    @Override
    public boolean onGUIClick(Player player, int slot, InventoryClickEvent e) {
        if (slot == 0 && currentData != null){
            loadEnchants(e.getInventory());
            return true;
        }
        if (slot == 19){
            loadEnchants(e.getInventory(), e.getCursor());
            return false;
        } else if (slot > 53){
            if (e.isShiftClick()){
                ItemStack clickedItem = e.getCurrentItem();
                ItemStack storedItem = e.getInventory().getItem(19);
                e.getInventory().setItem(19, clickedItem);
                e.setCurrentItem(storedItem);
                loadEnchants(e.getInventory(), clickedItem);
                return true;
            }
            return false;
        }
        int row = slot / 9;
        int col = slot % 9;
        if (row > 0 && row < 4 && col > 2 && col < 8){
            if (e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR){
                if (currentData == null){
                    if (e.getCurrentItem().getType() == Material.ENCHANTED_BOOK){
                        loadEnchant(e.getInventory(),
                                EnchantmentManager.getEnchantments().get(
                                        PersistentDataManager.getValueOrDefault(e.getCurrentItem(), PersistentDataType.STRING, "enchantment_id", "")));
                    }
                } else {
                    try {
                        CustomItemStack item = new CustomItemStack(e.getInventory().getItem(19));

                        int enchantment_level = PersistentDataManager.getValueOrDefault(e.getCurrentItem(), PersistentDataType.INTEGER, "enchantment_level", -1);

                        if (enchantment_level < 0){
                            PeakCraft.getPlugin().getLogger().info("Something went wrong and tbh I am not sure how!");
                            return true;
                        }

                        if (enchantment_level == 0){
                            item.removeEnchantment(currentData.getId());
                        } else if (item.canAddEnchant(currentData, enchantment_level)){
                            item.addEnchantment(currentData.getId(), enchantment_level);
                        }
                        e.getInventory().setItem(19, item);

                        loadEnchant(e.getInventory(), currentData);
                    } catch (ItemException ex) {
                        // there should be no case in which this is called but idk
                        ex.printStackTrace();
                    }
                }
            }

        }


        return true;
    }

    @Override
    public @NotNull Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, 54, "Enchant");

        this.fillInventory(inventory);

        inventory.setItem(19, new ItemStack(Material.AIR));

        loadEnchants(inventory, null);

        return inventory;
    }

    private void loadEnchant(Inventory inventory, EnchantmentData enchantmentData){
        ItemStack backItem = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backItem.getItemMeta();
        backMeta.setDisplayName("Go Back");
        backItem.setItemMeta(backMeta);
        inventory.setItem(0, backItem);

        currentData = enchantmentData;
        try {
            EnchantableItem item = (EnchantableItem) ItemManager.convertItem(inventory.getItem(19));
            loadEnchantmentPointsBar(inventory, item);

            // fill in the grid
            for (int i = 0; i < 15; i++){
                ItemStack levelIndicator;
                if (i > enchantmentData.getMaxLevel()){
                    levelIndicator = new ItemStack(Material.AIR);
                } else {
                    levelIndicator = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);

                    ItemMeta meta = levelIndicator.getItemMeta();
                    PersistentDataManager.setValue(meta, "enchantment_level", i);
                    int current_level = item.getEnchants().containsKey(enchantmentData.getId()) ? item.getEnchants().get(enchantmentData.getId()).getLevel() : 0;

                    meta.setDisplayName(enchantmentData.getDisplayName() + " " + i);
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);

                    ArrayList<String> lore = new ArrayList<>();

                    lore.add("Cost: " + enchantmentData.getCost(i) + " points");
                    if (i == 0) {
                        levelIndicator.setType(Material.BARRIER);
                        meta.setDisplayName("Remove " + enchantmentData.getDisplayName());
                    } else if (i < current_level){
                        levelIndicator.setType(Material.ORANGE_STAINED_GLASS_PANE);
                    } else if (i == current_level){
                        levelIndicator.setType(Material.LIME_STAINED_GLASS_PANE);
                        meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
                    } else if (!item.canAddEnchant(enchantmentData, i)){
                        levelIndicator.setType((Material.RED_STAINED_GLASS_PANE));
                        lore.add("");
                        lore.add("" + CustomColors.ERROR + ChatColor.BOLD + "Too expensive!");
                    } else {
                        levelIndicator.setType(Material.YELLOW_STAINED_GLASS_PANE);
                    }
                    meta.setLore(lore);
                    levelIndicator.setItemMeta(meta);
                }

                inventory.setItem(12 + (i % 5) + 9*(i/5), levelIndicator);
            }
        } catch (ItemException e) {
            e.printStackTrace();
        }
    }

    private void loadEnchantmentPointsBar(Inventory inventory){
        try {
            loadEnchantmentPointsBar(inventory, (EnchantableItem) ItemManager.convertItem(inventory.getItem(19)));
        } catch (ItemException|ClassCastException e) {
            loadEnchantmentPointsBar(inventory, null);
        }
    }

    private void loadEnchantmentPointsBar(Inventory inventory, EnchantableItem item){
        if (item == null){
            ItemStack itemStack = inventory.getItem(23);

            for (int i = 0; i < 9; i++){
                inventory.setItem(45 + i, itemStack);
            }
        } else {
            double percentUsed = ((double)item.getEnchantmentPoints()) / item.getMaxEnchantmentPoints();

            ItemStack itemStack = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);

            ItemMeta meta = itemStack.getItemMeta();

            meta.setDisplayName(item.getEnchantmentPoints() + "/" + item.getMaxEnchantmentPoints() + " points used");

            ArrayList<String> lore = new ArrayList<>();
            lore.add("You have " + (item.getMaxEnchantmentPoints() - item.getEnchantmentPoints()) + " free enchantment points remaining");
            lore.add("");
            lore.add("Remove enchantments to free up points or upgrade items to gain a higher max!");

            meta.setLore(lore);

            itemStack.setItemMeta(meta);

            for (int i = 0; i < 9; i++){
                if ((i+1.0) / 9 <= percentUsed){
                    itemStack.setType(Material.LIME_STAINED_GLASS_PANE);
                } else {
                    itemStack.setType(Material.WHITE_STAINED_GLASS_PANE);
                }
                inventory.setItem(45 + i, itemStack);
            }
        }
    }

    private void loadEnchants(Inventory inventory){
        loadEnchants(inventory, inventory.getItem(19));
    }

    private void loadEnchants(Inventory inventory, ItemStack enchantedItem) {
        currentData = null;

        inventory.setItem(0, getBackgroundItem());
        ArrayList<EnchantmentData> enchantments = new ArrayList<>();

        EnchantableItem item;

        try {
            item = (EnchantableItem) ItemManager.convertItem(enchantedItem);
        } catch (ItemException|ClassCastException e) {

            for (int i = 0; i < 15; i++){
                inventory.setItem(12 + (i % 5) + 9*(i/5), new ItemStack(Material.AIR));
            }

            ItemStack errorMessage = new ItemStack(Material.RED_STAINED_GLASS_PANE);

            ItemMeta meta = errorMessage.getItemMeta();
            String message;
            if (enchantedItem == null || enchantedItem.getType() == Material.AIR)
                message = "Place an item in the enchanting slot to the left";
            else if (e instanceof ItemException)
                message = "This is an invalid item";
            else
                message = "This item cannot be enchanted";
            meta.setDisplayName(CustomColors.ERROR + message);
            errorMessage.setItemMeta(meta);


            inventory.setItem(23, errorMessage);
            loadEnchantmentPointsBar(inventory, null);
            return;
        }

        loadEnchantmentPointsBar(inventory, item);

        for (EnchantmentData enchantment : EnchantmentManager.getEnchantments().values().stream().toList()){
            if (enchantment.validateEnchant(item) && !enchantment.isTreasure()){
                enchantments.add(enchantment);
            }
        }

        for (int i = 0; i < 15; i++){
            ItemStack enchantDisplay = new ItemStack(Material.ENCHANTED_BOOK);
            if (i >= enchantments.size()){
                enchantDisplay.setType(Material.AIR);
            } else {
                EnchantmentData enchantmentData = enchantments.get(i);

                ItemMeta meta = enchantDisplay.getItemMeta();
                meta.setDisplayName(enchantmentData.getDisplayName());

                List<String> lore = new ArrayList<>();
                if (item.getEnchants().containsKey(enchantmentData.getId()))
                    lore.add("Current enchantment level: " + item.getEnchants().get(enchantmentData.getId()).getLevel());

                lore.add("Click to select an enchantment level");

                PersistentDataManager.setValue(meta, "enchantment_id", enchantmentData.getId());

                meta.setLore(lore);

                enchantDisplay.setItemMeta(meta);
            }

            inventory.setItem(12 + (i % 5) + 9*(i/5), enchantDisplay);
        }
    }

    @Override
    public void onClose(Player player, InventoryCloseEvent e) {
        ItemStack enchantSlotItem = e.getInventory().getItem(19);

        if (enchantSlotItem != null && enchantSlotItem.getType() != Material.AIR){
            player.getInventory().addItem(enchantSlotItem);
        }
    }
}
