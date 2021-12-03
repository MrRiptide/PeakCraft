package io.github.mrriptide.peakcraft.items.enchantments;

import io.github.mrriptide.peakcraft.actions.ActionListener;
import io.github.mrriptide.peakcraft.items.EnchantableItem;
import org.apache.commons.lang.WordUtils;

public abstract class Enchantment implements ActionListener {
    protected final String id;
    protected final String displayName;
    protected final int level;

    public Enchantment(String id, String displayName, int level){
        this.id = id;
        this.displayName = displayName;
        this.level = level;
    }

    public Enchantment(int level){
        this("", "", level);
    }

    public abstract boolean validateEnchant(EnchantableItem item);

    public String getId(){
        return this.id;
    }

    public String getDisplayName(){
        return displayName;
    }

    public int getLevel() {
        return level;
    }
}
