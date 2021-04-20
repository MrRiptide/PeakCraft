package io.github.mrriptide.peakcraft.items;

import com.google.common.collect.Sets;
import io.github.mrriptide.peakcraft.items.enchantments.Enchantment;
import io.github.mrriptide.peakcraft.PeakCraft;
import io.github.mrriptide.peakcraft.items.enchantments.EnchantmentManager;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class Item {
    private final String id;
    private final String oreDict;
    private final String displayName;
    private final int rarity;
    private final String description;
    private final Material material;
    private final String type;
    private final HashMap<String, Integer> attributes;
    private HashMap<String, Integer> bakedAttributes;
    private HashMap<String, Integer> enchantments;

    public Item(String id){
        Item item = ItemManager.getItem(id);

        this.id = item.id;
        this.oreDict = item.oreDict;
        this.displayName = item.displayName;
        this.rarity = item.rarity;
        this.description = item.description;
        this.material = item.material;
        this.type = item.type;
        this.attributes = item.attributes;
        this.enchantments = new HashMap<>();
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
        this.enchantments = new HashMap<>();
    }

    public Item(String id, String oreDict, String displayName, int rarity, String description, Material material, String type, HashMap<String, Integer> attributes, HashMap<String, Integer> enchantments){
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
        PeakCraft.getPlugin().getLogger().info("Getting item from ItemStack");

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
        this.enchantments = new HashMap<>();
        // register enchants
        for (NamespacedKey key : Objects.requireNonNull(itemSource.getItemMeta()).getPersistentDataContainer().getKeys()){
            if (key.getKey().startsWith("enchant_")){
                addEnchantment(key.getKey().substring(8, key.getKey().length() - 6), getValueOrDefault(itemSource, PersistentDataType.INTEGER, key.getKey(), 0));
            }
        }
    }

    private <T> T getValueOrDefault(ItemStack itemStack, PersistentDataType type, String key, T defaultValue){
        NamespacedKey namespacedKey = new NamespacedKey(PeakCraft.getPlugin(), key);
        ItemMeta meta = itemStack.getItemMeta();
        T returnObject = null;
        if (meta != null){
            PersistentDataContainer container = meta.getPersistentDataContainer();
            if (container.has(namespacedKey, type)){
                returnObject = (T) container.get(namespacedKey, type);
            }
        }

        return (returnObject != null) ? returnObject : defaultValue;
    }

    private <T> void setValue(ItemMeta meta, PersistentDataType<T, T> type, String key, T value){
        NamespacedKey namespacedKey = new NamespacedKey(PeakCraft.getPlugin(), key);
        if (meta != null){
            PersistentDataContainer container = meta.getPersistentDataContainer();
            container.set(namespacedKey, type, value);
        }
    }

    private <T> void setValue(ItemStack itemStack, PersistentDataType<T, T> type, String key, T value){
        setValue(itemStack.getItemMeta(), type, key, value);
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

        // the item id
        setValue(meta, PersistentDataType.STRING, "ITEM_ID", id);
        // the enchantments
        for (Map.Entry<String, Integer> enchantment : enchantments.entrySet()){
            setValue(meta, PersistentDataType.INTEGER, "ENCHANT_" + enchantment.getKey().toUpperCase() + "_LEVEL", enchantment.getValue());
        }

        // put metadata on item

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

        bakeAttributes();

        // Attributes of item

        if (bakedAttributes.size() > 0){
            for (String attribute : bakedAttributes.keySet()){
                PeakCraft.getPlugin().getLogger().info(attribute);
                lore.add(ChatColor.DARK_PURPLE + attribute + ": " + getBakedAttribute(attribute));
            }

            lore.add("");
        }

        // Enchantments of item

        if (enchantments.size() > 0){
            for (Map.Entry<String, Integer> enchantment : enchantments.entrySet()){
                lore.add(WordUtils.capitalizeFully(enchantment.getKey()) + enchantment.getValue());
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

    public void bakeAttributes(){
        bakedAttributes = attributes;
        EnchantmentManager.bakeItem(this);
    }

    public int getBakedAttribute(String attributeName){
        return bakedAttributes.getOrDefault(attributeName, 0);
    }

    public void setBakedAttribute(String attributeName, int value){
        bakedAttributes.put(attributeName,value);
    }

    public HashMap<String, Integer> getEnchants(){
        return enchantments;
    }

    public void addEnchantment(String enchantment, int level){
        this.enchantments.put(enchantment.toLowerCase(), level);
    }

    public boolean removeEnchantment(String enchantment){
        return (this.enchantments.remove(enchantment.toLowerCase()) != null);
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
