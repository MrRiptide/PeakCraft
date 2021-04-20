package io.github.mrriptide.peakcraft.items.enchantments;

import io.github.mrriptide.peakcraft.items.Item;

public class EnchantmentSharpness extends Enchantment{
    public EnchantmentSharpness(){
        setName("Sharpness");
    }

    @Override
    public void bakeItemAttributes(Item item, int level) {
        item.setBakedAttribute("damage", item.getAttribute("sharpness") + level);
    }
}
