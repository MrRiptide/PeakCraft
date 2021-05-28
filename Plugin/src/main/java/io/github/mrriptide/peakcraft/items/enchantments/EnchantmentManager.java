package io.github.mrriptide.peakcraft.items.enchantments;

import io.github.mrriptide.peakcraft.PeakCraft;
import io.github.mrriptide.peakcraft.items.EnchantableItem;
import io.github.mrriptide.peakcraft.items.Item;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EnchantmentManager {
    private static HashMap<String, Enchantment> enchantments;

    public static void registerEnchantments(){
        enchantments = new HashMap<>();
        registerEnchantment(new EnchantmentHealthBoost());
        registerEnchantment(new EnchantmentSharpness());
    }

    private static void registerEnchantment(Enchantment enchantment){
        if (enchantments.containsKey(enchantment.getId().toLowerCase())){
            PeakCraft.getPlugin().getLogger().info("Failed to register " + enchantment.getId().toLowerCase() + " enchantment as it is already registered");
            return;
        }

        enchantments.put(enchantment.getId().toLowerCase(), enchantment);
        PeakCraft.getPlugin().getLogger().info("Successfully registered the " + enchantment.getId().toLowerCase() + " enchantment");
    }

    public static boolean validateEnchantment(String name){
        return enchantments.containsKey(name);
    }

    public static Enchantment getEnchantment(String name){
        return enchantments.get(name);
    }

    public static void bakeItem(EnchantableItem item){
        for (Map.Entry<String, Integer> enchantment : item.getEnchants().entrySet()){
            if (enchantments.containsKey(enchantment.getKey())){
                enchantments.get(enchantment.getKey()).bakeItemAttributes(item, enchantment.getValue());
            }
        }
    }

    public static HashMap<String, Enchantment> getEnchantments(){
        return enchantments;
    }
}
