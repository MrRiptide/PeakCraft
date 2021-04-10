//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package io.github.mrriptide.peakcraft.recipes;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import io.github.mrriptide.peakcraft.PeakCraft;
import net.minecraft.server.v1_16_R3.*;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftRecipe;
import org.bukkit.inventory.RecipeChoice;

public class ShapedNMSRecipe extends NMSRecipe {
    private final int width;
    private final int height;
    private final NonNullList<RecipeItemChoice> items;
    private final ItemStack result;
    private final MinecraftKey key;
    private final String group;

    public ShapedNMSRecipe(MinecraftKey minecraftkey, String s, int i, int j, NonNullList<RecipeItemChoice> nonnulllist, ItemStack itemstack) {
        this.key = minecraftkey;
        this.group = s;
        this.width = i;
        this.height = j;
        this.items = nonnulllist;
        this.result = itemstack;
    }

    public Recipes<?> g() {
        return Recipes.CRAFTING;
    }

    public org.bukkit.inventory.Recipe toBukkitRecipe() {
        throw new NotImplementedException();
        /*io.github.mrriptide.peakcraft.recipes.ShapedRecipe recipe;
        CraftItemStack result = CraftItemStack.asCraftMirror(this.result);
        RecipeItem[][] recipeItems = null;
        recipe = new io.github.mrriptide.peakcraft.recipes.ShapedRecipe(recipeItems, new RecipeItem("air"));
        recipe.setGroup(this.group);
        label40:
        switch(this.height) {
            case 1:
                switch(this.width) {
                    case 1:
                        recipe.setShape(new String[]{"a"});
                        break label40;
                    case 2:
                        recipe.setShape(new String[]{"ab"});
                        break label40;
                    case 3:
                        recipe.setShape(new String[]{"abc"});
                    default:
                        break label40;
                }
            case 2:
                switch(this.width) {
                    case 1:
                        recipe.setShape(new String[]{"a", "b"});
                        break label40;
                    case 2:
                        recipe.setShape(new String[]{"ab", "cd"});
                        break label40;
                    case 3:
                        recipe.setShape(new String[]{"abc", "def"});
                    default:
                        break label40;
                }
            case 3:
                switch(this.width) {
                    case 1:
                        recipe.setShape(new String[]{"a", "b", "c"});
                        break;
                    case 2:
                        recipe.setShape(new String[]{"ab", "cd", "ef"});
                        break;
                    case 3:
                        recipe.setShape(new String[]{"abc", "def", "ghi"});
                }
        }

        char c = 'a';

        for(Iterator var5 = this.items.iterator(); var5.hasNext(); ++c) {
            RecipeItemChoice list = (RecipeItemChoice)var5.next();
            RecipeChoice choice = CraftRecipe.toBukkit(list);
            if (choice != null) {
                recipe.setIngredient(c, choice);
            }
        }

        return recipe;*/
    }

    public MinecraftKey getKey() {
        return this.key;
    }

    // so i need to convert
    public RecipeSerializer<?> getRecipeSerializer() {
        PeakCraft.getPlugin().getLogger().info("getRecipeSerializer called");
        Thread.dumpStack();
        PacketPlayOutRecipeUpdate
        net.minecraft.server.v1_16_R3.PacketEncoder
        NonNullList<RecipeItemStack> ingredients = NonNullList.a(width*height, RecipeItemStack.a);

        for (int i = 0; i < items.size(); i++){
            RecipeItemStack ingredient = RecipeItemStack.a;
            ingredient.buildChoices();
            ingredient.choices = items.get(i).choices;
            ingredients.set(i, ingredient);
        }

        ShapedRecipes convertedRecipe = new ShapedRecipes(getKey(), getGroup(), width, height, ingredients, result);
        return convertedRecipe.getRecipeSerializer();
    }

    public ItemStack getResult() {
        return this.result;
    }

    public NonNullList<RecipeItemStack> a() {
        throw new NotImplementedException();
        //return this.items;
    }

    public boolean a(InventoryCrafting inventorycrafting, World world) {
        for(int i = 0; i <= inventorycrafting.g() - this.width; ++i) {
            for(int j = 0; j <= inventorycrafting.f() - this.height; ++j) {
                if (this.a(inventorycrafting, i, j, true)) {
                    return true;
                }

                if (this.a(inventorycrafting, i, j, false)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean a(InventoryCrafting inventorycrafting, int i, int j, boolean flag) {
        for(int k = 0; k < inventorycrafting.g(); ++k) {
            for(int l = 0; l < inventorycrafting.f(); ++l) {
                int i1 = k - i;
                int j1 = l - j;
                RecipeItemChoice recipeitemstack = RecipeItemChoice.a;
                if (i1 >= 0 && j1 >= 0 && i1 < this.width && j1 < this.height) {
                    if (flag) {
                        recipeitemstack = (RecipeItemChoice)this.items.get(this.width - i1 - 1 + j1 * this.width);
                    } else {
                        recipeitemstack = (RecipeItemChoice)this.items.get(i1 + j1 * this.width);
                    }
                }

                if (!recipeitemstack.test(inventorycrafting.getItem(k + l * inventorycrafting.g()))) {
                    return false;
                }
            }
        }

        return true;
    }

    public ItemStack a(InventoryCrafting inventorycrafting) {
        return this.getResult().cloneItemStack();
    }

    public int i() {
        return this.width;
    }

    public int j() {
        return this.height;
    }

    private static NonNullList<RecipeItemChoice> b(String[] astring, Map<String, RecipeItemChoice> map, int i, int j) {
        NonNullList<RecipeItemChoice> nonnulllist = NonNullList.a(i * j, RecipeItemChoice.a);
        Set<String> set = Sets.newHashSet(map.keySet());
        set.remove(" ");

        for(int k = 0; k < astring.length; ++k) {
            for(int l = 0; l < astring[k].length(); ++l) {
                String s = astring[k].substring(l, l + 1);
                RecipeItemChoice recipeitemstack = (RecipeItemChoice)map.get(s);
                if (recipeitemstack == null) {
                    throw new JsonSyntaxException("Pattern references symbol '" + s + "' but it's not defined in the key");
                }

                set.remove(s);
                nonnulllist.set(l + i * k, recipeitemstack);
            }
        }

        if (!set.isEmpty()) {
            throw new JsonSyntaxException("Key defines symbols that aren't used in pattern: " + set);
        } else {
            return nonnulllist;
        }
    }

    @VisibleForTesting
    static String[] a(String... astring) {
        int i = 2147483647;
        int j = 0;
        int k = 0;
        int l = 0;

        for(int i1 = 0; i1 < astring.length; ++i1) {
            String s = astring[i1];
            i = Math.min(i, a(s));
            int j1 = b(s);
            j = Math.max(j, j1);
            if (j1 < 0) {
                if (k == i1) {
                    ++k;
                }

                ++l;
            } else {
                l = 0;
            }
        }

        if (astring.length == l) {
            return new String[0];
        } else {
            String[] astring1 = new String[astring.length - l - k];

            for(int k1 = 0; k1 < astring1.length; ++k1) {
                astring1[k1] = astring[k1 + k].substring(i, j + 1);
            }

            return astring1;
        }
    }

    private static int a(String s) {
        int i;
        for(i = 0; i < s.length() && s.charAt(i) == ' '; ++i) {
        }

        return i;
    }

    private static int b(String s) {
        int i;
        for(i = s.length() - 1; i >= 0 && s.charAt(i) == ' '; --i) {
        }

        return i;
    }

    private static String[] b(JsonArray jsonarray) {
        String[] astring = new String[jsonarray.size()];
        if (astring.length > 3) {
            throw new JsonSyntaxException("Invalid pattern: too many rows, 3 is maximum");
        } else if (astring.length == 0) {
            throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
        } else {
            for(int i = 0; i < astring.length; ++i) {
                String s = ChatDeserializer.a(jsonarray.get(i), "pattern[" + i + "]");
                if (s.length() > 3) {
                    throw new JsonSyntaxException("Invalid pattern: too many columns, 3 is maximum");
                }

                if (i > 0 && astring[0].length() != s.length()) {
                    throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
                }

                astring[i] = s;
            }

            return astring;
        }
    }

    private static Map<String, RecipeItemChoice> c(JsonObject jsonobject) {
        Map<String, RecipeItemChoice> map = Maps.newHashMap();
        Iterator iterator = jsonobject.entrySet().iterator();

        while(iterator.hasNext()) {
            Entry<String, JsonElement> entry = (Entry)iterator.next();
            if (((String)entry.getKey()).length() != 1) {
                throw new JsonSyntaxException("Invalid key entry: '" + (String)entry.getKey() + "' is an invalid symbol (must be 1 character only).");
            }

            if (" ".equals(entry.getKey())) {
                throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
            }

            map.put((String)entry.getKey(), RecipeItemChoice.a((JsonElement)entry.getValue()));
        }

        map.put(" ", RecipeItemChoice.a);
        return map;
    }

    public static ItemStack a(JsonObject jsonobject) {
        String s = ChatDeserializer.h(jsonobject, "item");
        Item item = (Item)IRegistry.ITEM.getOptional(new MinecraftKey(s)).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown item '" + s + "'");
        });
        if (jsonobject.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
        } else {
            int i = ChatDeserializer.a(jsonobject, "count", 1);
            return new ItemStack(item, i);
        }
    }
}
