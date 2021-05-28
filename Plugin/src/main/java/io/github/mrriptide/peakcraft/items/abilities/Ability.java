package io.github.mrriptide.peakcraft.items.abilities;

import io.github.mrriptide.peakcraft.entity.PlayerWrapper;
import io.github.mrriptide.peakcraft.util.CustomColors;
import org.apache.commons.lang.WordUtils;

import java.util.ArrayList;

public abstract class Ability {
    private String name;
    private String displayName;
    private String description;
    private int manaCost;

    public Ability(String name, String displayName, String description, int manaCost){
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.manaCost = manaCost;
    }

    public ArrayList<String> getLore(){
        ArrayList<String> lore = new ArrayList<>();
        lore.add(CustomColors.MAGIC_DESCRIPTION + "Item Ability: " + displayName);

        String[] wrapped_description = WordUtils.wrap(description, 30, "\n", true).split("\n");
        for (String line : wrapped_description){
            lore.add(CustomColors.DESCRIPTION + line);
        }
        lore.add(CustomColors.SUB_DESCRIPTION + "Mana Cost: " + CustomColors.MANA + manaCost );

        lore.add("");

        return lore;
    }

    public String getName(){
        return name;
    }

    public int getManaCost(){
        return manaCost;
    }

    public abstract void useAbility(PlayerWrapper player);
}
