package io.github.mrriptide.peakcraft.items.enchantments;

import io.github.mrriptide.peakcraft.PeakCraft;
import io.github.mrriptide.peakcraft.items.Item;

public class EnchantmentSharpness extends Enchantment{
    public EnchantmentSharpness(){
        super("sharpness", "Sharpness");
    }

    @Override
    public void bakeItemAttributes(Item item, int level) {
        item.setBakedAttribute("damage", item.getBakedAttribute("damage") * 1.05 * level);
    }
}
