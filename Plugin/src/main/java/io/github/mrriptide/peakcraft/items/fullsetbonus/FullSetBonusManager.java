package io.github.mrriptide.peakcraft.items.fullsetbonus;

import java.util.HashMap;

public abstract class FullSetBonusManager {
    private static HashMap<String, FullSetBonus> bonuses = new HashMap<>();

    public static void registerSet(FullSetBonus bonus){
        bonuses.put(bonus.getName(), bonus);
    }

    public static boolean validSet(String name){
        return bonuses.containsKey(name);
    }

    public static FullSetBonus getSet(String name){
        if (!bonuses.containsKey(name)){
            return null;
        }
        return bonuses.get(name);
    }
}
