package io.github.mrriptide.peakcraft.items;

import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Stream;

public class ArmorItem extends EnchantableItem {

    public ArmorItem(Item item){
        super(item);
    }

    public static Item loadFromHashMap(HashMap<String, String> itemData){
        ArmorItem item = new ArmorItem(Item.loadFromHashMap(itemData));

        item.setAttribute("health", Double.parseDouble(itemData.getOrDefault("health", "0.0")));
        item.setAttribute("defense", Double.parseDouble(itemData.getOrDefault("defense", "0.0")));

        return item;
    }

    public static boolean validateType(String type){
        return Arrays.asList("armor", "chestplate", "helmet", "leggings", "boots").contains(type);
    }


}
