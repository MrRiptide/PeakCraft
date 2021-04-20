package io.github.mrriptide.peakcraft.items.enchantments;

import io.github.mrriptide.peakcraft.items.Item;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.WordUtils;

import java.util.HashMap;

public abstract class Enchantment {
    private String name;

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    /*public String getDisplayName(){
        return WordUtils.capitalizeFully(getName()) + getLevel();
    }*/

    public abstract void bakeItemAttributes(Item item, int level);
}
