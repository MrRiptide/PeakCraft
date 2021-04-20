package io.github.mrriptide.peakcraft;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Item {
    private String id;
    private String oreDict;
    private String displayName;
    private int rarity;
    private String description;
    private Material material;
    private String type;
    private HashMap<String, Integer> attributes;
    private ArrayList<Enchantment> enchantments;

    public Item(String id){
        Item item = ItemManager.getItem(id);

        this.id = item.id;
        this.displayName = item.displayName;
        this.rarity = item.rarity;
        this.description = item.description;
        this.material = item.material;
        this.type = item.type;
        this.attributes = item.attributes;
        this.enchantments = new ArrayList<>();
    }

    public Item(String id, String oreDict, String displayName, int rarity, String description, Material material, String type, HashMap<String, Integer> attributes){
        this.id = id;
        this.oreDict = oreDict;
        this.displayName = displayName;
        this.rarity = rarity;
        this.description = description;
        this.material = material;
        this.type = (type != null && !type.isEmpty()) ? type : "item";
        this.attributes = attributes;
        this.enchantments = new ArrayList<>();
    }

    public Item(String id, String oreDict, String displayName, int rarity, String description, Material material, String type, HashMap<String, Integer> attributes, ArrayList<Enchantment> enchantments){
        this.id = id;
        this.oreDict = oreDict;
        this.displayName = displayName;
        this.rarity = rarity;
        this.description = description;
        this.material = material;
        this.type = (type != null && !type.isEmpty()) ? type : "item";
        this.attributes = attributes;
        this.enchantments = enchantments;
    }

    public Item(ItemStack itemSource){
        // Get ID of the item from the ItemStack

        // Default option
        this.id = getValueOrDefault(itemSource, PersistentDataType.STRING, "ITEM_ID", itemSource.getType().name());

        assert this.id != null;
        Item default_item = ItemManager.getItem(this.id);

        this.oreDict = default_item.oreDict;
        this.rarity = default_item.rarity;
        this.displayName = default_item.displayName;
        this.description = default_item.description;
        this.type = default_item.type;
        this.material = default_item.material;
        this.attributes = default_item.attributes;
        this.enchantments = default_item.enchantments;
    }

    private <T> T getValueOrDefault(ItemStack itemStack, PersistentDataType type, String key, T defaultValue){
        NamespacedKey itemIDKey = new NamespacedKey(PeakCraft.getPlugin(), key);
        ItemMeta meta = itemStack.getItemMeta();
        T returnObject = null;
        if (meta != null){
            PersistentDataContainer container = meta.getPersistentDataContainer();
            if (container.has(itemIDKey, type)){
                returnObject = (T) container.get(itemIDKey, type);
            }
        }

        return (returnObject != null) ? returnObject : defaultValue;
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

        // Apply enchant glint if it is enchanted
        if (enchantments.size() > 0){
            meta.addEnchant(org.bukkit.enchantments.Enchantment.SILK_TOUCH, 1, true);
        }

        // Hide things
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

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

        // Description of item
        if (description != null && description.length() > 0){
            String[] wrapped_description = WordUtils.wrap(description, 30, "\n", true).split("\n");
            for (String line : wrapped_description){
                lore.add("§7" + line);
            }
            lore.add("");
        }

        // Attributes of item

        if (attributes.size() > 0){
            for (String attribute : attributes.keySet()){
                PeakCraft.getPlugin().getLogger().info(attribute);
                lore.add(attribute + ": " + getAttribute(attribute));
            }

            lore.add("");
        }

        // Enchantments of item

        if (enchantments.size() > 0){
            for (Enchantment enchantment : enchantments){
                lore.add(enchantment.getDisplayName());
            }

            lore.add("");
        }

        // Rarity of item
        lore.add(getRarityColor() + getRarityName().toUpperCase() + " " + type.toUpperCase());

        return lore;
    }

    public int getAttribute(String attributeName){
        return attributes.getOrDefault(attributeName, 0);
    }

    public void setAttribute(String attributeName, int value){
        attributes.put(attributeName, value);
    }

    public ArrayList<Enchantment> getEnchants(){
        return enchantments;
    }

    public void addEnchantment(Enchantment enchantment){
        this.enchantments.add(enchantment);
    }

    public boolean removeEnchantment(Enchantment enchantment){
        return this.enchantments.remove(enchantment);
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
