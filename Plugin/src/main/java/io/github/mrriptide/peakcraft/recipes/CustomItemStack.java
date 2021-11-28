package io.github.mrriptide.peakcraft.recipes;

import io.github.mrriptide.peakcraft.exceptions.ItemException;
import io.github.mrriptide.peakcraft.items.EnchantableItem;
import io.github.mrriptide.peakcraft.items.Item;
import io.github.mrriptide.peakcraft.items.ItemManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class CustomItemStack extends ItemStack {
    private Item item;

    // I don't want to use this extensively yet, I just want to first check if it causes any issues if I use it in recipes
    // could this be a fix for the item pickup stuff? actually yeah that sounds great!
    public CustomItemStack(ItemStack stack) throws ItemException {
        super(stack);
        this.item = ItemManager.convertItem(stack);
        this.item.updateItemStack(this);
    }

    public CustomItemStack(Item item){
        super(Material.AIR);
        this.item = item;
        this.item.updateItemStack(this);
    }

    public void update(){
        this.item.updateItemStack(this);
    }


    public void addEnchantment(String enchantment, int level){
        if (item instanceof EnchantableItem){
            ((EnchantableItem)item).addEnchantment(enchantment, level);
        } else {
            throw new IllegalArgumentException("This item is not enchantable");
        }
    }

    public boolean removeEnchantment(String enchantment){
        if (item instanceof EnchantableItem){
            return ((EnchantableItem)item).removeEnchantment(enchantment);
        } else {
            throw new IllegalArgumentException("This item is not enchantable");
        }
    }


    public Item getItem(){
        return item;
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
            try {
                CustomItemStack customStack = (stack instanceof CustomItemStack) ? (CustomItemStack) stack : new CustomItemStack(stack);
                return this.item.equals(customStack.item);
            } catch (ItemException e) {
                return false;
            }
        }
    }
}
