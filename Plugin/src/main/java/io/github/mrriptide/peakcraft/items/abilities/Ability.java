package io.github.mrriptide.peakcraft.items.abilities;

import io.github.mrriptide.peakcraft.entity.player.PlayerWrapper;
import io.github.mrriptide.peakcraft.items.abilities.triggers.AbilityTrigger;
import io.github.mrriptide.peakcraft.util.CustomColors;
import org.apache.commons.lang.WordUtils;

import java.util.ArrayList;

public abstract class Ability {
    private String name;
    private AbilityType type;
    private String displayName;
    private String description;
    private int manaCost;
    private double cooldown;

    public enum AbilityType {
        RIGHT_CLICK,
        LEFT_CLICK,
        PASSIVE
    }

    public Ability(String name, AbilityType type, String displayName, String description, int manaCost, double cooldown){
        this.name = name;
        this.type = type;
        this.displayName = displayName;
        this.description = description;
        this.manaCost = manaCost;
        this.cooldown = cooldown;
    }

    public ArrayList<String> getLore(){
        ArrayList<String> lore = new ArrayList<>();
        lore.add(CustomColors.MAGIC_DESCRIPTION + "Item Ability: " + displayName + " (" + WordUtils.capitalizeFully(type.name().replace("_", " ")) + ")");

        String[] wrapped_description = WordUtils.wrap(description, 30, "\n", true).split("\n");
        for (String line : wrapped_description){
            lore.add(CustomColors.DESCRIPTION + line);
        }
        if (manaCost != 0)
            lore.add(CustomColors.SUB_DESCRIPTION + "Mana Cost: " + CustomColors.MANA + manaCost );
        if (cooldown != 0)
            lore.add(CustomColors.SUB_DESCRIPTION + "Cooldown: " + CustomColors.COOLDOWN + cooldown + " seconds");

        return lore;
    }

    public String getName(){
        return name;
    }

    public int getManaCost(){
        return manaCost;
    }

    public double getCooldown(){
        return cooldown;
    }

    public AbilityType getType() {
        return type;
    }

    public boolean canUseAbility(PlayerWrapper player, AbilityTrigger trigger){
        if (player.getMana() >= manaCost){
            return true;
        } else {
            player.getSource().sendMessage(CustomColors.ERROR + "Not enough mana!");
            return false;
        }
    }

    public abstract void useAbility(PlayerWrapper player, AbilityTrigger trigger);
}
