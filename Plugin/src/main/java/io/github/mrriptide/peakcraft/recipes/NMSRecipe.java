package io.github.mrriptide.peakcraft.recipes;

import io.github.mrriptide.peakcraft.PeakCraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;

public abstract class NMSRecipe<C extends Container> implements Recipe<C> {
    private RecipeItem result;
    private String group;
    private ResourceLocation id;

    public void setResult(RecipeItem result) {this.result = result;}

    public RecipeItem getResult() {
        return result;
    }

    @Override
    public ItemStack getResultItem(){
        return null;//CraftItemStack.asNMSCopy(result.getItemStack());
    }

    public RecipeItem getResultRecipeItem(){return result;}

    public void setGroup(String group){
        this.group = group;
    }

    public String getGroup(){
        return group;
    }

    @Override
    public ResourceLocation getId(){
        return this.id;
    }

    public void setId(ResourceLocation id){
        this.id = id;
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return false;
    }
}
