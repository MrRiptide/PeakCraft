package io.github.mrriptide.peakcraft.recipes;

import net.minecraft.server.v1_16_R3.RecipeItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.function.Predicate;

public class RecipeItemChoice {
    private ArrayList<RecipeItem> choices;

    public RecipeItemChoice(String id){
        this.choices = new ArrayList<>();
        choices.add(new RecipeItem(id));
    }

    public RecipeItemChoice(RecipeItem recipeItem){
        this.choices = new ArrayList<>();
        choices.add(recipeItem);
    }

    public RecipeItemChoice(ArrayList<RecipeItem> choices){
        this.choices = choices;
    }

    /*public ItemStack getItemStack() {
        return recipeItem.getItemStack();
    }*/

    public boolean test(ItemStack itemStack) {
        RecipeItem craftItem = new RecipeItem(itemStack);
        for (RecipeItem recipeItem : choices){
            if (craftItem.getCount() >= recipeItem.getCount() && (craftItem.getId().equals(recipeItem.getId())
                    || (!recipeItem.getOreDict().equals("") && recipeItem.getOreDict().equals(craftItem.getOreDict()))))
                return true;
        }
        return false;
    }
}
