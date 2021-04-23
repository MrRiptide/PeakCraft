package io.github.mrriptide.peakcraft.items.enchantments;

import io.github.mrriptide.peakcraft.PeakCraft;
import io.github.mrriptide.peakcraft.items.Item;

public class EnchantmentSharpness extends Enchantment{
    public EnchantmentSharpness(){
        setName("sharpness");
    }

    @Override
    public void bakeItemAttributes(Item item, int level) {
        item.setBakedAttribute("damage", item.getBakedAttribute("damage") + level);
        PeakCraft.getPlugin().getLogger().info(String.valueOf(item.getBakedAttribute("damage")));
    }
}
