package io.github.mrriptide.peakcraft.items;

public class ItemStack{
    private final Item item;
    private int amount;

    public ItemStack(Item item){
        this.item = item;
        this.amount = 1;
    }

    public org.bukkit.inventory.ItemStack toBukkit(){
        org.bukkit.inventory.ItemStack itemStack = item.getItemStack();
        itemStack.setAmount(amount);
        return itemStack;
    }

    public void setAmount(int amount){
        this.amount = amount;
    }

    public Item getItem(){
        return this.item;
    }

}
