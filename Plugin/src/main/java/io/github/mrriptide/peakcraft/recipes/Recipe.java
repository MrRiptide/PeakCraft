package io.github.mrriptide.peakcraft.recipes;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import io.github.mrriptide.peakcraft.PeakCraft;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public abstract class Recipe implements org.bukkit.inventory.Recipe{
    private RecipeItem result;
    private String group;
    private NamespacedKey key;

    @JsonSetter("result")
    public void setResult(RecipeItem result) {this.result = result;}

    @JsonIgnore
    public ItemStack getResult(){
        return null;//(result != null) ? result.getItemStack() : (new RecipeItem("air")).getItemStack();
    }

    @JsonGetter("result")
    public RecipeItem getResultRecipeItem(){return result;}

    public void setGroup(String group){
        this.group = group;
    }

    public String getGroup(){
        return group;
    }

    @JsonIgnore
    public NamespacedKey getKey(){
        return this.key;
    }

    public void setKey(NamespacedKey key){
        this.key = key;
    }

    public void setKey(String name){
        setKey(new NamespacedKey(PeakCraft.instance, name));
    }

    public abstract boolean test(Recipe recipe);

    public abstract net.minecraft.world.item.crafting.Recipe<?> toNMS(String recipe_name);
}
