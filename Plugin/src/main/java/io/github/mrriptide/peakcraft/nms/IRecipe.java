package io.github.mrriptide.peakcraft.nms;

import io.github.mrriptide.peakcraft.recipes.RecipeItem;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.bukkit.inventory.Recipe;

public interface IRecipe<C extends Inventory> extends net.minecraft.world.item.crafting.Recipe<C> {
    boolean a(C var1, ServerLevel var2);

    ItemStack a(C var1);

    ItemStack getResult();

    default NonNullList<ItemStack> b(C c0) {
        NonNullList<ItemStack> nonnulllist = NonNullList.create();

        for(int i = 0; i < nonnulllist.size(); ++i) {
            Item item = c0.getItem(i).getItem();
            /*if (item()) {
                nonnulllist.set(i, new ItemStack(item.getCraftingRemainingItem()));
            }*/
        }

        return nonnulllist;
    }

    default NonNullList<ItemStack> getBasicNonNullList() {
        return NonNullList.create();
    }

    default boolean isComplex() {
        return false;
    }

    ResourceLocation getKey();

    RecipeSerializer<?> getRecipeSerializer();

    //Recipes<?> g();

    Recipe toBukkitRecipe();
}
