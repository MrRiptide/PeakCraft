package io.github.mrriptide.peakcraft.items;

import java.util.HashMap;

public class WeaponItem extends EnchantableItem {

    public WeaponItem(){

    }

    public static Item loadFromHashMap(HashMap<String, String> itemData){
        WeaponItem item = (WeaponItem) Item.loadFromHashMap(itemData);

        item.setAttribute("damage", Double.parseDouble(itemData.get("damage")));

        return item;
    }

}
