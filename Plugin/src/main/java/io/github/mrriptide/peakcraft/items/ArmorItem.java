package io.github.mrriptide.peakcraft.items;

import java.util.HashMap;

public class ArmorItem extends EnchantableItem {
    public static Item loadFromHashMap(HashMap<String, String> itemData){
        ArmorItem item = (ArmorItem) Item.loadFromHashMap(itemData);

        item.setAttribute("health", Double.parseDouble(itemData.get("health")));
        item.setAttribute("defense", Double.parseDouble(itemData.get("defense")));

        return item;
    }



}
