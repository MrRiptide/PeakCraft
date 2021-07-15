package io.github.mrriptide.peakcraft.items.abilities;

import io.github.mrriptide.peakcraft.PeakCraft;
import io.github.mrriptide.peakcraft.entity.PlayerWrapper;
import io.github.mrriptide.peakcraft.items.abilities.triggers.AbilityTrigger;
import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerEvent;

import java.util.HashMap;

public class AbilityManager {
    private static HashMap<String, Ability> abilities = new HashMap<>();

    public static void registerAbility(Ability ability){
        abilities.put(ability.getName(), ability);
        PeakCraft.getPlugin().getLogger().info("Successfully registered the \"" + ability.getName() + "\"");
    }

    public static boolean validateAbility(String name){
        if (!abilities.containsKey(name))
                PeakCraft.getPlugin().getLogger().warning("Invalid ability \"" + name + "\"");
        return abilities.containsKey(name);
    }

    public static Ability getAbility(String name){
        return abilities.get(name);
    }

    public static void triggerAbility(String name, PlayerWrapper source, AbilityTrigger trigger){
        if (validateAbility(name)){
            triggerAbility(getAbility(name), source, trigger);
        }
    }

    public static void triggerAbility(Ability ability, PlayerWrapper player, AbilityTrigger trigger){
        if (ability.canUseAbility(player, trigger)){
            player.reduceMana(ability.getManaCost());
            ability.useAbility(player, trigger);
        }
    }
}
