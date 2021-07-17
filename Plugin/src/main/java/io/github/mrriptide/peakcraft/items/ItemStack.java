package io.github.mrriptide.peakcraft.items;

import io.github.mrriptide.peakcraft.exceptions.ItemException;

public class ItemStack{
    private final Item item;

    private int amount;

    public ItemStack(Item item, int amount){
        this.item = item;
        this.amount = amount;
    }

    public ItemStack(Item item){
        this(item, 1);
    }

    public ItemStack(org.bukkit.inventory.ItemStack itemStack) throws ItemException {
        this(ItemManager.convertItem(itemStack), itemStack.getAmount());
    }

    public org.bukkit.inventory.ItemStack toBukkit(){
        org.bukkit.inventory.ItemStack itemStack = item.getItemStack();
        itemStack.setAmount(amount);
        return itemStack;
    }

    public void setAmount(int amount){
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public Item getItem(){
        return this.item;
    }

}
