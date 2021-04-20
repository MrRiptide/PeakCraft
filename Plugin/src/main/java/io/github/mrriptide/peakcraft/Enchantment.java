package io.github.mrriptide.peakcraft;

import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.WordUtils;

import java.util.HashMap;

public class Enchantment {
    private String name;
    private int level;
    private HashMap<String, Integer> attributeModifiers;

    public Enchantment(){
        name = "Unknown Enchantment";
        level = 0;
        attributeModifiers = new HashMap<>();
    }

    public Enchantment(String name, int level){
        this.name = name;
        this.level = level;
        this.attributeModifiers = new HashMap<>();
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public String getDisplayName(){
        return WordUtils.capitalizeFully(getName()) + getLevel();
    }

    public void setLevel(int level){
        this.level = level;
    }

    public int getLevel(){
        return this.level;
    }

    public void setAttributeModifiers(HashMap<String, Integer> modifiers){
        this.attributeModifiers = modifiers;
    }

    public void setAttributeModifier(String name, int value){
        attributeModifiers.put(name, value);
    }

    public HashMap<String, Integer> getAttributeModifiers(){
        return this.attributeModifiers;
    }
}
