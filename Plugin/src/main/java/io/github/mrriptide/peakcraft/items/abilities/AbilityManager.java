package io.github.mrriptide.peakcraft.items.abilities;

import io.github.mrriptide.peakcraft.PeakCraft;
import io.github.mrriptide.peakcraft.entity.PlayerWrapper;
import io.github.mrriptide.peakcraft.util.CustomColors;
import org.bukkit.Bukkit;

import java.util.HashMap;

public class AbilityManager {
    private static HashMap<String, Ability> abilities = new HashMap<>();

    public static void registerAbility(Ability ability){
        abilities.put(ability.getName(), ability);
        PeakCraft.getPlugin().getLogger().info("Successfully registered the \"" + ability.getName() + "\"");
    }

    public static boolean validateAbility(String name){
        if (!abilities.containsKey(name))
                Bukkit.broadcastMessage("Invalid ability \"" + name + "\"");
        return abilities.containsKey(name);
    }

    public static Ability getAbility(String name){
        return abilities.get(name);
    }

    public static void triggerAbility(String name, PlayerWrapper source){
        if (validateAbility(name)){
            triggerAbility(getAbility(name), source);
        }
    }

    public static void triggerAbility(Ability ability, PlayerWrapper player){
        if (player.reduceMana(ability.getManaCost())){
            ability.useAbility(player);
        } else{
            player.getSource().sendMessage(CustomColors.ERROR + "Not enough mana!");
        }
    }
}
