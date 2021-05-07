package io.github.mrriptide.peakcraft.items;

import io.github.mrriptide.peakcraft.items.enchantments.EnchantmentManager;
import io.github.mrriptide.peakcraft.util.PersistentDataManager;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EnchantableItem extends Item {
    protected HashMap<String, Double> attributes = null;
    protected HashMap<String, Double> bakedAttributes = null;
    protected HashMap<String, Integer> enchantments = null;

    public EnchantableItem(){

    }

    public EnchantableItem(String id, String oreDict, String displayName, int rarity, String description, Material material, String type, HashMap<String, Double> attributes, HashMap<String, Integer> enchantments){
        super(id, oreDict, displayName, rarity, description, material, type);

        this.attributes = attributes;
        this.enchantments = enchantments;
        bakeAttributes();
    }

    public EnchantableItem(String id){
        super(id);
        EnchantableItem item = (EnchantableItem) ItemManager.getItem(id);

        this.attributes = item.attributes;
        this.enchantments = new HashMap<>();

        bakeAttributes();
    }

    public EnchantableItem(String id, String oreDict, String displayName, int rarity, String description, Material material, String type, HashMap<String, Double> attributes){
        this(
                id,
                oreDict,
                displayName,
                rarity,
                description,
                material,
                type,
                attributes,
                new HashMap<>()
        );

        bakeAttributes();
    }

    public EnchantableItem(ItemStack itemSource){
        super(itemSource);
        // Get ID of the item from the ItemStack

        // Default option
        this.id = PersistentDataManager.getValueOrDefault(itemSource, PersistentDataType.STRING, "ITEM_ID", itemSource.getType().name());

        assert this.id != null;
        EnchantableItem default_item = (EnchantableItem) ItemManager.getItem(this.id);

        this.attributes = default_item.attributes;
        this.enchantments = new HashMap<>();
        // register enchants
        for (NamespacedKey key : Objects.requireNonNull(itemSource.getItemMeta()).getPersistentDataContainer().getKeys()){
            if (key.getKey().startsWith("enchant_")){
                addEnchantment(key.getKey().substring(8, key.getKey().length() - 6), PersistentDataManager.getValueOrDefault(itemSource, PersistentDataType.INTEGER, key.getKey(), 0));
            }
        }

        bakeAttributes();
    }

    public ArrayList<String> getLore(){
        ArrayList<String> lore = super.getLore();

        lore.remove(lore.size()-1);

        // Attributes of item

        HashMap<String, ChatColor> attributeColor = new HashMap<>();
        attributeColor.put("damage", ChatColor.DARK_RED);
        attributeColor.put("defense", ChatColor.GREEN);
        attributeColor.put("health", ChatColor.RED);
        if (attributes.size() > 0){
            for (String attribute : attributes.keySet()){
                lore.add(attributeColor.getOrDefault(attribute, ChatColor.DARK_PURPLE) + "" + ChatColor.BOLD + WordUtils.capitalizeFully(attribute) + ChatColor.RESET + ChatColor.WHITE + ": " + getAttribute(attribute));
            }

            lore.add("");
        }

        // Enchantments of item

        if (enchantments.size() > 0){
            for (Map.Entry<String, Integer> enchantment : enchantments.entrySet()){
                lore.add(ChatColor.LIGHT_PURPLE +
                        ((EnchantmentManager.validateEnchantment(enchantment.getKey()) ?
                                EnchantmentManager.getEnchantment(enchantment.getKey()).getDisplayName() :
                                "Unknown Enchantment")
                                + " " + enchantment.getValue()));
            }

            lore.add("");
        }

        // Rarity of item
        lore.add(getRarityColor() + getRarityName().toUpperCase() + " " + type.toUpperCase());

        return lore;
    }

    public double getAttribute(String attributeName){
        return attributes.getOrDefault(attributeName.toLowerCase(), 0.0);
    }

    public void setAttribute(String attributeName, double value){
        attributes.put(attributeName.toLowerCase(), value);
    }

    public void bakeAttributes(){
        bakedAttributes = attributes;
        EnchantmentManager.bakeItem(this);
    }

    public double getBakedAttribute(String attributeName){
        return bakedAttributes.getOrDefault(attributeName.toLowerCase(), 0.0);
    }

    public void setBakedAttribute(String attributeName, double value){
        bakedAttributes.put(attributeName.toLowerCase(),value);
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

    @Override
    public ItemStack getItemStack() {
        ItemStack stack = super.getItemStack();

        ItemMeta meta = stack.getItemMeta();

        // Apply enchant glint if it is enchanted
        if (enchantments.size() > 0){
            meta.addEnchant(Enchantment.DAMAGE_ALL, 1, true);
        }

        // the item id
        PersistentDataManager.setValue(meta, PersistentDataType.STRING, "ITEM_ID", id);
        // the enchantments
        for (Map.Entry<String, Integer> enchantment : enchantments.entrySet()){
            PersistentDataManager.setValue(meta, PersistentDataType.INTEGER, "ENCHANT_" + enchantment.getKey().toUpperCase() + "_LEVEL", enchantment.getValue());
        }

        stack.setItemMeta(meta);

        return stack;
    }

    @Override
    public Item clone() {
        EnchantableItem clonedItem = (EnchantableItem) super.clone();



        clonedItem.attributes = new HashMap<>();
        for (Map.Entry<String, Double> entry : attributes.entrySet()){
            clonedItem.attributes.put(entry.getKey(), entry.getValue());
        }
        clonedItem.enchantments = new HashMap<>();
        for (Map.Entry<String, Integer> entry : enchantments.entrySet()){
            clonedItem.enchantments.put(entry.getKey(), entry.getValue());
        }


        return clonedItem;
    }
}
