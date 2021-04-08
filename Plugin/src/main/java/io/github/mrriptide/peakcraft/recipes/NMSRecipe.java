package io.github.mrriptide.peakcraft.recipes;

import io.github.mrriptide.peakcraft.PeakCraft;
import net.minecraft.server.v1_16_R3.IRecipe;
import net.minecraft.server.v1_16_R3.InventoryCrafting;
import net.minecraft.server.v1_16_R3.ItemStack;
import net.minecraft.server.v1_16_R3.MinecraftKey;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftNamespacedKey;

public abstract class NMSRecipe implements IRecipe<InventoryCrafting>{
    private RecipeItem result;
    private String group;
    private MinecraftKey key;

    public void setResult(RecipeItem result) {this.result = result;}

    @Override
    public ItemStack getResult(){
        return CraftItemStack.asNMSCopy(result.getItemStack());
    }

    public RecipeItem getResultItem(){return result;}

    public void setGroup(String group){
        this.group = group;
    }

    public String getGroup(){
        return group;
    }

    @Override
    public MinecraftKey getKey(){
        return this.key;
    }

    public void setKey(MinecraftKey key){
        this.key = key;
    }

    public abstract boolean test(NMSRecipe recipe);
}
