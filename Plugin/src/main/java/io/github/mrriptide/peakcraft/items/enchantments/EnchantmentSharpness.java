package io.github.mrriptide.peakcraft.items.enchantments;

import io.github.mrriptide.peakcraft.PeakCraft;
import io.github.mrriptide.peakcraft.items.EnchantableItem;
import io.github.mrriptide.peakcraft.items.Item;
import io.github.mrriptide.peakcraft.items.WeaponItem;

public class EnchantmentSharpness extends Enchantment{
    public EnchantmentSharpness(){
        super("sharpness", "Sharpness");
    }

    @Override
    public boolean validateEnchant(EnchantableItem item) {
        return (item instanceof WeaponItem);
    }

    @Override
    public void bakeItemAttributes(EnchantableItem item, int level) {
        item.setBakedAttribute("damage", item.getBakedAttribute("damage") * 1.05 * level);
    }
}
