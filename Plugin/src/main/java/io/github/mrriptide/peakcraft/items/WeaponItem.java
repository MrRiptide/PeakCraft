package io.github.mrriptide.peakcraft.items;

import java.util.Arrays;
import java.util.HashMap;

public class WeaponItem extends EnchantableItem {

    public WeaponItem(){

    }

    public WeaponItem(Item item){
        super(item);
    }

    public static Item loadFromHashMap(HashMap<String, String> itemData){
        WeaponItem item = new WeaponItem(Item.loadFromHashMap(itemData));

        item.setAttribute("damage", Double.parseDouble(itemData.getOrDefault("damage", "10.0")));

        return item;
    }

    public static boolean validateType(String type){
        return Arrays.asList("weapon", "sword").contains(type);
    }

}
