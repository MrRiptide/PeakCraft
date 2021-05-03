package io.github.mrriptide.peakcraft.items.enchantments;

import io.github.mrriptide.peakcraft.PeakCraft;
import io.github.mrriptide.peakcraft.items.Item;

public class EnchantmentHealthBoost extends Enchantment{
    public EnchantmentHealthBoost(){
        setName("sharpness");
    }

    @Override
    public void bakeItemAttributes(Item item, int level) {
        item.setBakedAttribute("health", item.getBakedAttribute("health") + 50 * level);
    }
}
