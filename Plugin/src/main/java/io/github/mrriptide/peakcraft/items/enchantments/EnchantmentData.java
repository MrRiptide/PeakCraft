package io.github.mrriptide.peakcraft.items.enchantments;

import io.github.mrriptide.peakcraft.actions.Action;
import io.github.mrriptide.peakcraft.actions.ActionListener;
import io.github.mrriptide.peakcraft.items.EnchantableItem;

public abstract class EnchantmentData {
    protected String id;
    protected String displayName;
    protected boolean isTreasure;
    protected int maxLevel;

    protected EnchantmentData(String id, String displayName, int maxLevel, boolean isTreasure){
        this.id = id;
        this.displayName = displayName;
        this.maxLevel = maxLevel;
        this.isTreasure = isTreasure;
    }

    protected EnchantmentData(String id, String displayName, int maxLevel){
        this(id, displayName, maxLevel, false);
    }

    protected EnchantmentData(String id, String displayName){
        this(id, displayName, -1);
    }

    public ActionListener.PriorityLevel getListeningLevel(){
        return ActionListener.PriorityLevel.MIDDLE;
    }

    public abstract boolean listensTo(Action action);

    public abstract void onAction(Action action, int level);

    public abstract boolean validateEnchant(EnchantableItem item);

    public abstract int getCost(int level);

    public String getDisplayName(){
        return displayName;
    }

    public boolean isTreasure(){
        return isTreasure;
    }

    public int getMaxLevel(){
        return maxLevel;
    }

    public String getId(){
        return id;
    }
}
