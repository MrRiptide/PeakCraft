package io.github.mrriptide.peakcraft.items.enchantments;

import io.github.mrriptide.peakcraft.PeakCraft;
import io.github.mrriptide.peakcraft.items.EnchantableItem;
import io.github.mrriptide.peakcraft.items.Item;
import io.github.mrriptide.peakcraft.items.WeaponItem;

public class EnchantmentSharpness extends Enchantment{
    protected String id = "sharpness";
    protected String displayName = "Sharpness";

    public EnchantmentSharpness(){
        super("sharpness", "Sharpness");
    }

    @Override
    public boolean validateEnchant(EnchantableItem item) {
        return (item instanceof WeaponItem);
    }

    @Override
    public void bakeItemAttributes(EnchantableItem item, int level) {
        item.setBakedAttribute("damage", item.getBakedAttribute("damage") * Math.pow(1.05, level));
    }
}
