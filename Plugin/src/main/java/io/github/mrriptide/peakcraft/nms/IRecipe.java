package io.github.mrriptide.peakcraft.nms;

import net.minecraft.server.v1_16_R3.*;
import org.bukkit.inventory.Recipe;

public interface IRecipe<C extends IInventory> extends net.minecraft.server.v1_16_R3.IRecipe<C> {
    boolean a(C var1, World var2);

    ItemStack a(C var1);

    ItemStack getResult();

    default NonNullList<ItemStack> b(C c0) {
        NonNullList<ItemStack> nonnulllist = NonNullList.a(c0.getSize(), ItemStack.b);

        for(int i = 0; i < nonnulllist.size(); ++i) {
            Item item = c0.getItem(i).getItem();
            if (item.p()) {
                nonnulllist.set(i, new ItemStack(item.getCraftingRemainingItem()));
            }
        }

        return nonnulllist;
    }

    default NonNullList<RecipeItemStack> getBasicNonNullList() {
        return NonNullList.a();
    }

    default boolean isComplex() {
        return false;
    }

    MinecraftKey getKey();

    RecipeSerializer<?> getRecipeSerializer();

    Recipes<?> g();

    Recipe toBukkitRecipe();
}
