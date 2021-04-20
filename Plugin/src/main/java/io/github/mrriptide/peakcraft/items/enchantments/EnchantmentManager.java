package io.github.mrriptide.peakcraft.items.enchantments;

import io.github.mrriptide.peakcraft.items.Item;

import java.util.HashMap;
import java.util.Map;

public class EnchantmentManager {
    private static HashMap<String, Enchantment> enchantments = new HashMap<>();

    public static boolean registerEnchantment(Enchantment enchantment){
        if (enchantments.containsKey(enchantment.getName())){
            return false;
        }

        enchantments.put(enchantment.getName(), enchantment);
        return true;
    }

    public static void bakeItem(Item item){
        for (Map.Entry<String, Integer> enchantment : item.getEnchants().entrySet()){
            if (enchantments.containsKey(enchantment.getKey())){
                enchantments.get(enchantment.getKey()).bakeItemAttributes(item, enchantment.getValue());
            }
        }
    }
}
