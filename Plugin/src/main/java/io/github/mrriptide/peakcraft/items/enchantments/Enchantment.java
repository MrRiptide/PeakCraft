package io.github.mrriptide.peakcraft.items.enchantments;

import io.github.mrriptide.peakcraft.items.EnchantableItem;
import io.github.mrriptide.peakcraft.items.Item;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.WordUtils;

import java.util.HashMap;

public abstract class Enchantment {
    private String name;
    private String displayName;

    public Enchantment(String name, String displayName){
        this.name = name;
        this.displayName = displayName;
    }

    public Enchantment(String name){
        this.name = name;
        this.displayName = WordUtils.capitalizeFully(name);
    }

    public abstract boolean validateEnchant(EnchantableItem item);

    public void setName(String name){
        this.name = name.toLowerCase();
    }

    public String getName(){
        return this.name;
    }

    public String getDisplayName(){
        return displayName;
    }

    public abstract void bakeItemAttributes(EnchantableItem item, int level);
}
