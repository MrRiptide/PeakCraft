package io.github.mrriptide.peakcraft;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Objects;

public class Item {
    private String id;
    private String oreDict;
    private String displayName;
    private int rarity;
    private String description;
    private Material material;

    public Item(String id){
        Item item = ItemManager.getItem(id);

        this.id = item.id;
        this.displayName = item.displayName;
        this.rarity = item.rarity;
        this.description = item.description;
        this.material = item.material;
    }

    public Item(String id, String oreDict, String displayName, int rarity, String description, Material material){
        this.id = id;
        this.oreDict = oreDict;
        this.displayName = displayName;
        this.rarity = rarity;
        this.description = description;
        this.material = material;
    }

    public Item(ItemStack itemSource){
        // Get ID of the item from the ItemStack

        // Default option
        this.id = itemSource.getType().name();

        // If a PeakCraft-specific id is defined, use that instead
        NamespacedKey itemIDKey = new NamespacedKey(PeakCraft.getPlugin(), "ITEM_ID");
        ItemMeta meta = itemSource.getItemMeta();
        if (meta != null){
            PersistentDataContainer container = meta.getPersistentDataContainer();
            if (container.has(itemIDKey, PersistentDataType.STRING)){
                this.id = container.get(itemIDKey, PersistentDataType.STRING);
            }
        }

        Item default_item = ItemManager.getItem(this.id);

        this.oreDict = default_item.oreDict;
        this.rarity = default_item.rarity;
        this.displayName = default_item.displayName;
        this.description = default_item.description;
        this.material = default_item.material;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(id, item.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public ItemStack getItemStack(){
        ItemStack item = new ItemStack(material);

        //PeakCraft.getPlugin().getLogger().info("Item: " + item);

        ItemMeta meta = item.getItemMeta();
        if (meta == null){
            return item;
        }

        // Add the lore to the item
        meta.setLore(getLore());

        // Set the custom name
        meta.setDisplayName(getRarityColor() + displayName);

        item.setItemMeta(meta);

        return item;
    }

    public ItemStack convertItem(ItemStack item){

        ItemMeta meta = item.getItemMeta();

        // Add the lore to the item
        assert meta != null;
        meta.setLore(getLore());

        // Set the custom name
        meta.setDisplayName(getFormattedDisplayName());

        item.setItemMeta(meta);

        return item;
    }

    public String getID(){
        return id;
    }

    public String getOreDict(){
        return oreDict;
    }

    public String getDisplayName(){
        return displayName;
    }

    public String getFormattedDisplayName(){
        return getRarityColor() + displayName;
    }

    public String getDescription(){
        return description;
    }

    public ArrayList<String> getLore(){
        ArrayList<String> lore = new ArrayList<>();

        if (description != null && description.length() > 0){
            String[] wrapped_description = WordUtils.wrap(description, 30, "\n", true).split("\n");
            for (String line : wrapped_description){
                lore.add("§7" + line);
            }
            lore.add("");
        }

        lore.add(getRarityColor() + getRarityName().toUpperCase() + " ITEM"); // @TODO: Should be changed from Item to a value to support different types of items

        return lore;
    }

    private ChatColor getRarityColor(){
        ChatColor[] colors = {
                ChatColor.DARK_RED, // Broken
                ChatColor.GRAY, // Common
                ChatColor.GREEN, // Uncommon
                ChatColor.BLUE, // Rare
                ChatColor.DARK_PURPLE, // Epic
                ChatColor.GOLD, // Legendary
                ChatColor.LIGHT_PURPLE, // Mythic
                ChatColor.AQUA}; // Relic
        return colors[rarity];
    }

    private String getRarityName() {
        String[] names = {"Broken", "Common", "Uncommon", "Rare", "Epic", "Legendary", "Mythic", "Relic"};

        return ChatColor.BOLD + names[rarity];
    }
}
