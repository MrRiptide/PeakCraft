package io.github.mrriptide.peakcraft.items.enchantments;

import io.github.mrriptide.peakcraft.PeakCraft;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class EnchantmentManager {
    private static HashMap<String, Class<? extends Enchantment>> enchantments;

    public static void registerEnchantments(){
        enchantments = new HashMap<>();
        registerEnchantment(EnchantmentHealthBoost.class);
        registerEnchantment(EnchantmentSharpness.class);
    }

    private static void registerEnchantment(Class<? extends Enchantment> enchantment){
        Enchantment instance = getEnchantment(enchantment, -1);

        if (enchantments.containsKey(instance.getId().toLowerCase())){
            PeakCraft.getPlugin().getLogger().info("Failed to register " + instance.getId().toLowerCase() + " enchantment as it is already registered");
            return;
        }

        enchantments.put(instance.getId().toLowerCase(), enchantment);
        PeakCraft.getPlugin().getLogger().info("Successfully registered the " + instance.getId().toLowerCase() + " enchantment");
    }

    public static boolean validateEnchantment(String name){
        return enchantments.containsKey(name);
    }

    public static Enchantment getEnchantment(String name, int level){
        Class<? extends Enchantment> enchantmentClass = enchantments.get(name);
        return getEnchantment(enchantmentClass, level);
    }

    public static Enchantment getEnchantment(Class<? extends Enchantment> enchantmentClass, int level){
        try {
            Constructor<? extends Enchantment> constructor =  enchantmentClass.getDeclaredConstructor(int.class);
            return constructor.newInstance(level);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static HashMap<String, Class<? extends Enchantment>> getEnchantments(){
        return enchantments;
    }
}
