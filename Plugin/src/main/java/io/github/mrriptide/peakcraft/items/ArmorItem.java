package io.github.mrriptide.peakcraft.items;

import java.util.HashMap;

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



}
