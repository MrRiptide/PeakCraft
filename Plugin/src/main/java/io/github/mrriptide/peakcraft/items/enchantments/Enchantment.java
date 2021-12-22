package io.github.mrriptide.peakcraft.items.enchantments;

import io.github.mrriptide.peakcraft.actions.Action;
import io.github.mrriptide.peakcraft.actions.ActionListener;
import io.github.mrriptide.peakcraft.items.EnchantableItem;

public class Enchantment implements ActionListener {
    protected EnchantmentData enchantmentData;
    protected int level;

    public Enchantment(EnchantmentData enchantmentData, int level){
        this.enchantmentData = enchantmentData;
        this.level = level;
    }

    public String getDisplayName(){
        return enchantmentData.getDisplayName();
    }

    public String getId(){
        return enchantmentData.getId();
    }

    public int getLevel() {
        return level;
    }

    public int getCost() {
        return enchantmentData.getCost(level);
    }

    @Override
    public PriorityLevel getListeningLevel(){
        return enchantmentData.getListeningLevel();
    }

    @Override
    public boolean listensTo(Action action) {
        return enchantmentData != null && enchantmentData.listensTo(action);
    }

    @Override
    public void onAction(Action action) {
        enchantmentData.onAction(action, level);
    }
}
