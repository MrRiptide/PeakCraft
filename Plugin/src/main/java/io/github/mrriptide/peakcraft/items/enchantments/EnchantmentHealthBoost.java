package io.github.mrriptide.peakcraft.items.enchantments;

import io.github.mrriptide.peakcraft.PeakCraft;
import io.github.mrriptide.peakcraft.items.ArmorItem;
import io.github.mrriptide.peakcraft.items.EnchantableItem;
import io.github.mrriptide.peakcraft.items.Item;

public class EnchantmentHealthBoost extends Enchantment{
    public EnchantmentHealthBoost(){
        super("healthboost", "Health Boost");
    }

    public boolean validateEnchant(EnchantableItem item){
        return (item instanceof ArmorItem);
    }

    @Override
    public void bakeItemAttributes(EnchantableItem item, int level) {
        item.setBakedAttribute("health", item.getBakedAttribute("health") + 50 * level);
    }
}
