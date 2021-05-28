package io.github.mrriptide.peakcraft.items;

import io.github.mrriptide.peakcraft.items.fullsetbonus.FullSetBonus;
import io.github.mrriptide.peakcraft.items.fullsetbonus.FullSetBonusManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Stream;

public class ArmorItem extends EnchantableItem {
    private String set;

    public ArmorItem(Item item){
        super(item);
    }

    public static Item loadFromHashMap(HashMap<String, String> itemData){
        ArmorItem item = new ArmorItem(Item.loadFromHashMap(itemData));

        item.setAttribute("health", Double.parseDouble(itemData.getOrDefault("health", "0.0")));
        item.setAttribute("defense", Double.parseDouble(itemData.getOrDefault("defense", "0.0")));
        item.set = itemData.getOrDefault("set", "");

        return item;
    }

    public static boolean validateType(String type){
        return Arrays.asList("armor", "chestplate", "helmet", "leggings", "boots").contains(type);
    }

    @Override
    public Item clone(){
        ArmorItem item = new ArmorItem(super.clone());
        item.set = set;
        return item;
    }

    public String getSetName(){
        return this.set;
    }

    public FullSetBonus getSet(){
        if (FullSetBonusManager.validSet(this.set))
            return FullSetBonusManager.getSet(this.set);
        else
            return null;
    }

}
