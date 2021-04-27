package io.github.mrriptide.peakcraft.util;

import io.github.mrriptide.peakcraft.PeakCraft;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftCreature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PersistentDataManager {

    public static <T> T getValueOrDefault(PersistentDataContainer container, PersistentDataType type, String key, T defaultValue){
        NamespacedKey namespacedKey = new NamespacedKey(PeakCraft.getPlugin(), key);

        T returnObject = null;
        if (container.has(namespacedKey, type)){
            returnObject = (T) container.get(namespacedKey, type);
        }

        return (returnObject != null) ? returnObject : defaultValue;
    }

    public static <T> T getValueOrDefault(ItemMeta meta, PersistentDataType type, String key, T defaultValue){
        if (meta != null){
            return getValueOrDefault(meta.getPersistentDataContainer(), type, key, defaultValue);
        } else {
            return null;
        }
    }

    public static <T> T getValueOrDefault(ItemStack itemStack, PersistentDataType type, String key, T defaultValue){
        return getValueOrDefault(itemStack.getItemMeta(), type, key, defaultValue);
    }

    public static <T> T getValueOrDefault(Entity entity, PersistentDataType type, String key, T defaultValue){
        return getValueOrDefault(entity.getPersistentDataContainer(), type, key, defaultValue);
    }

    public static <T> void setValue(PersistentDataContainer container, PersistentDataType<T, T> type, String key, T value){
        NamespacedKey namespacedKey = new NamespacedKey(PeakCraft.getPlugin(), key);

        container.set(namespacedKey, type, value);
    }

    public static <T> void setValue(ItemMeta meta, PersistentDataType<T, T> type, String key, T value){
        if (meta != null){
            setValue(meta.getPersistentDataContainer(), type, key, value);
        }
    }

    public static <T> void setValue(ItemStack itemStack, PersistentDataType<T, T> type, String key, T value){
        setValue(itemStack.getItemMeta(), type, key, value);
    }

    public static <T> void setValue(Entity entity, PersistentDataType<T, T> type, String key, T value){
        setValue(entity.getPersistentDataContainer(), type, key, value);
    }
}
