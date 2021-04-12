package io.github.mrriptide.peakcraft.recipes;

import io.github.mrriptide.peakcraft.PeakCraft;
import net.minecraft.server.v1_16_R3.IRecipe;
import net.minecraft.server.v1_16_R3.InventoryCrafting;
import net.minecraft.server.v1_16_R3.MinecraftKey;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftNamespacedKey;
import org.bukkit.inventory.ItemStack;

public abstract class Recipe implements org.bukkit.inventory.Recipe{
    private RecipeItem result;
    private String group;
    private NamespacedKey key;

    public void setResult(RecipeItem result) {this.result = result;}

    public ItemStack getResult(){return result.getItemStack();}

    public RecipeItem getResultRecipeItem(){return result;}

    public void setGroup(String group){
        this.group = group;
    }

    public String getGroup(){
        return group;
    }

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

    public abstract IRecipe<?> toNMS(String recipe_name);
}
