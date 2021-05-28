package io.github.mrriptide.peakcraft.items.fullsetbonus;

import io.github.mrriptide.peakcraft.entity.PlayerWrapper;
import io.github.mrriptide.peakcraft.util.CustomColors;
import org.apache.commons.lang.WordUtils;

import java.util.ArrayList;

public abstract class FullSetBonus {
    private String name;
    private String description;

    public FullSetBonus(String name, String description){
        this.name = name;
        this.description = description;
    }

    public abstract void applyBonus(PlayerWrapper playerWrapper);

    public String getName(){
        return name;
    }

    public String getDescription(){
        return description;
    }

    public ArrayList<String> getLore(){
        ArrayList<String> lore = new ArrayList<>();
        lore.add(CustomColors.MAGIC_DESCRIPTION + "Full Set Bonus: ");

        String[] wrapped_description = WordUtils.wrap(description, 30, "\n", true).split("\n");
        for (String line : wrapped_description){
            lore.add(CustomColors.DESCRIPTION + line);
        }

        lore.add("");

        return lore;
    }
}
