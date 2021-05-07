package io.github.mrriptide.peakcraft.items;

import io.github.mrriptide.peakcraft.items.enchantments.EnchantmentManager;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class EnchantableItem extends Item {
    protected HashMap<String, Double> attributes = null;
    protected HashMap<String, Double> bakedAttributes = null;
    protected HashMap<String, Integer> enchantments = null;

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
}
