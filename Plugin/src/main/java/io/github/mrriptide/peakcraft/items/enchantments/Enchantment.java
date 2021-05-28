package io.github.mrriptide.peakcraft.items.enchantments;

import io.github.mrriptide.peakcraft.items.EnchantableItem;
import org.apache.commons.lang.WordUtils;

public abstract class Enchantment {
    protected String id;
    protected String displayName;

    public Enchantment(String id, String displayName){
        this.id = id;
        this.displayName = displayName;
    }

    public Enchantment(String id){
        this.id = id;
        this.displayName = WordUtils.capitalizeFully(id);
    }

    public abstract boolean validateEnchant(EnchantableItem item);

    public void setId(String id){
        this.id = id.toLowerCase();
    }

    public String getId(){
        return this.id;
    }

    public String getDisplayName(){
        return displayName;
    }

    public abstract void bakeItemAttributes(EnchantableItem item, int level);
}
