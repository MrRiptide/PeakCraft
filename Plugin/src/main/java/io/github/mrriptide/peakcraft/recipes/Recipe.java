package io.github.mrriptide.peakcraft.recipes;

import org.bukkit.inventory.ItemStack;

public abstract class Recipe {
    private RecipeItem result;
    private String group;

    public void setResult(RecipeItem result) {this.result = result;}

    public RecipeItem getResult(){
        return result;
    }

    public void setGroup(String group){
        this.group = group;
    }

    public String getGroup(){
        return group;
    }

    public abstract boolean test(Recipe recipe);
}
