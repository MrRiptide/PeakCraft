package io.github.mrriptide.peakcraft.util;

import io.github.mrriptide.peakcraft.PeakCraft;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PersistentDataManager {

    public static <T> T getValueOrDefault(PersistentDataContainer container, PersistentDataType<T,T> type, String key, T defaultValue){
        NamespacedKey namespacedKey = new NamespacedKey(PeakCraft.getPlugin(), key);

        T returnObject = null;
        if (container.has(namespacedKey, type)){
            returnObject = (T) container.get(namespacedKey, type);
        }

        return (returnObject != null) ? returnObject : defaultValue;
    }

    public static <T> T getValueOrDefault(ItemMeta meta, PersistentDataType<T,T> type, String key, T defaultValue){
        if (meta != null){
            return getValueOrDefault(meta.getPersistentDataContainer(), type, key, defaultValue);
        } else {
            return null;
        }
    }

    public static <T> T getValueOrDefault(ItemStack itemStack, PersistentDataType<T,T> type, String key, T defaultValue){
        return getValueOrDefault(itemStack.getItemMeta(), type, key, defaultValue);
    }

    public static <T> T getValueOrDefault(Entity entity, PersistentDataType<T,T> type, String key, T defaultValue){
        return getValueOrDefault(entity.getPersistentDataContainer(), type, key, defaultValue);
    }

    public static Attribute getAttribute(Entity entity, String baseKey, double defaultBase){
        return getAttribute(entity.getPersistentDataContainer(), baseKey, defaultBase);
    }

    public static Attribute getAttribute(PersistentDataContainer container, String baseKey, double defaultBase){
        return new Attribute(
                getValueOrDefault(container, PersistentDataType.DOUBLE, baseKey, defaultBase),
                getValueOrDefault(container, PersistentDataType.DOUBLE, baseKey + "Additive", 0.0),
                getValueOrDefault(container, PersistentDataType.DOUBLE, baseKey + "Multi", 0.0)
        );
    }

    public static void setAttribute(Entity entity, String baseKey, Attribute attribute){
        setAttribute(entity.getPersistentDataContainer(), baseKey, attribute);
    }

    public static void setAttribute(PersistentDataContainer container, String baseKey, Attribute attribute){
        setValue(container, baseKey, attribute.getBase());
        setValue(container, baseKey + "Additive", attribute.getAdditive());
        setValue(container, baseKey + "Multi", attribute.getMulti());
    }

    public static <T> void setValue(PersistentDataContainer container, String key, T value){
        NamespacedKey namespacedKey = new NamespacedKey(PeakCraft.getPlugin(), key);
        PersistentDataType<T,T> type;
        if (value instanceof Byte){
            type = (PersistentDataType<T, T>) PersistentDataType.BYTE;
        } else if (value instanceof Byte[]){
            type = (PersistentDataType<T, T>) PersistentDataType.BYTE_ARRAY;
        } else if (value instanceof Double){
            type = (PersistentDataType<T, T>) PersistentDataType.DOUBLE;
        } else if (value instanceof Float){
            type = (PersistentDataType<T, T>) PersistentDataType.FLOAT;
        } else if (value instanceof Integer){
            type = (PersistentDataType<T, T>) PersistentDataType.INTEGER;
        } else if (value instanceof Integer[]){
            type = (PersistentDataType<T, T>) PersistentDataType.INTEGER_ARRAY;
        } else if (value instanceof Long){
            type = (PersistentDataType<T, T>) PersistentDataType.LONG;
        } else if (value instanceof Short[]){
            type = (PersistentDataType<T, T>) PersistentDataType.SHORT;
        } else if (value instanceof String){
            type = (PersistentDataType<T, T>) PersistentDataType.STRING;
        } else if (value instanceof PersistentDataContainer){
            type = (PersistentDataType<T, T>) PersistentDataType.TAG_CONTAINER;
        } else if (value instanceof PersistentDataContainer[]){
            type = (PersistentDataType<T, T>) PersistentDataType.TAG_CONTAINER_ARRAY;
        } else {
            throw new IllegalArgumentException("The value passed in does not have an associated persistent data type defined");
        }

        container.set(namespacedKey, type, value);
    }

    public static <T> void setValue(ItemMeta meta, String key, T value){
        if (meta != null){
            setValue(meta.getPersistentDataContainer(), key, value);
        }
    }

    public static <T> void setValue(ItemStack itemStack, String key, T value){
        setValue(itemStack.getItemMeta(), key, value);
    }

    public static <T> void setValue(Entity entity, String key, T value){
        setValue(entity.getPersistentDataContainer(), key, value);
    }
}
