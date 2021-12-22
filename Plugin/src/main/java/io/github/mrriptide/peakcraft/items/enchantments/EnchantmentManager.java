package io.github.mrriptide.peakcraft.items.enchantments;

import io.github.mrriptide.peakcraft.PeakCraft;

import java.util.HashMap;

public class EnchantmentManager {
    private static HashMap<String, EnchantmentData> enchantments;

    public static void registerEnchantments(){
        enchantments = new HashMap<>();
        registerEnchantment(new EnchantmentHealthBoost());
        registerEnchantment(new EnchantmentSharpness());
        registerEnchantment(new EnchantmentBaneOfArthropods());
        registerEnchantment(new EnchantmentBlastProtection());
        registerEnchantment(new EnchantmentDepthStrider());
        registerEnchantment(new EnchantmentEnvironmentalProtection());
        registerEnchantment(new EnchantmentFireAspect());
        registerEnchantment(new EnchantmentFireProtection());
        registerEnchantment(new EnchantmentFrostWalker());
        registerEnchantment(new EnchantmentProjectileProtection());
        registerEnchantment(new EnchantmentProtection());
        registerEnchantment(new EnchantmentSmite());
        registerEnchantment(new EnchantmentSoulSpeed());
        registerEnchantment(new EnchantmentThorns());
    }

    private static void registerEnchantment(EnchantmentData enchantment){
        enchantments.put(enchantment.getId(), enchantment);
        PeakCraft.getPlugin().getLogger().info("Successfully registered the " + enchantment.getId().toLowerCase() + " enchantment");
    }

    public static boolean validateEnchantment(String name){
        return enchantments.containsKey(name);
    }

    public static Enchantment getEnchantment(String name, int level){
        return getEnchantment(enchantments.get(name), level);
    }

    public static Enchantment getEnchantment(EnchantmentData enchantmentData, int level){
        return new Enchantment(enchantmentData, level);
    }

    public static HashMap<String, EnchantmentData> getEnchantments(){
        return enchantments;
    }
}
