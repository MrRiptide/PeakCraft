package io.github.mrriptide.peakcraft.recipes;

import io.github.mrriptide.peakcraft.exceptions.ItemException;
import io.github.mrriptide.peakcraft.items.Item;
import io.github.mrriptide.peakcraft.items.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class CustomItemStack extends ItemStack {
    private Item item;

    // I don't want to use this extensively yet, I just want to first check if it causes any issues if I use it in recipes
    // could this be a fix for the item pickup stuff? actually yeah that sounds great!
    public CustomItemStack(ItemStack stack){
        super(stack);
        try {
            this.item = ItemManager.convertItem(stack);
        } catch (ItemException e) {
            e.printStackTrace();
        }
    }


    /**
     * Tests if the given ItemStack is a valid entry in a recipe
     *
     * @param stack the ItemStack to compare against
     *
     * */
    public boolean isSimilar(@Nullable ItemStack stack) {
        if (stack == null) {
            return false;
        } else if (stack == this) {
            return true;
        } else { // other would have to have a greater amount
            // test the amount
            if (stack.getAmount() > this.getAmount())
                return false;
            // get the custom stack if not already passed it
            CustomItemStack customStack = (stack instanceof CustomItemStack) ? (CustomItemStack) stack : new CustomItemStack(stack);
            return this.item.equals(customStack.item);
        }
    }
}
