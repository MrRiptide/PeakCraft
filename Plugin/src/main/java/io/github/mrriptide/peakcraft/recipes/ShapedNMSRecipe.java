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

import net.minecraft.server.v1_16_R3.*;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftNamespacedKey;

public class ShapedNMSRecipe extends NMSRecipe {
    private final int width;
    private final int height;
    private final NonNullList<RecipeItemChoice> items;
    private final ItemStack result;
    private final MinecraftKey key;
    private final String group;

    public ShapedNMSRecipe(MinecraftKey minecraftkey, String s, int i, int j, NonNullList<RecipeItemChoice> nonnulllist, ItemStack itemstack) {
        //PeakCraft.getPlugin().getLogger().info("1");
        this.key = minecraftkey;
        this.group = s;
        this.width = i;
        this.height = j;
        this.items = nonnulllist;
        this.result = itemstack;
    }

    public org.bukkit.inventory.Recipe toBukkitRecipe() {
        // Make RecipeItem array
        RecipeItem[][] ingredientArray = new RecipeItem[height][width];

        for (int i = 0; i < height; i++){
            for (int j = 0; j < width; j++){
                ingredientArray[i][j] = new RecipeItem(CraftItemStack.asBukkitCopy(items.get(i*3 + j).choices[0]));
            }
        }

        // Get RecipeItem result

        RecipeItem result = new RecipeItem(CraftItemStack.asBukkitCopy(getResult()));

        ShapedRecipe recipe = new ShapedRecipe(ingredientArray, result);

        recipe.setGroup(this.group);
        recipe.setKey(CraftNamespacedKey.fromMinecraft(this.key));

        return recipe;
    }

    public MinecraftKey getKey() {
        //PeakCraft.getPlugin().getLogger().info("4");
        return this.key;
    }

    // so i need to convert
    public RecipeSerializer<?> getRecipeSerializer() {
        //PeakCraft.getPlugin().getLogger().info("5");
        Thread.dumpStack();

        RecipeSerializer<ShapedNMSRecipe> serializer = RecipeSerializer.a((String)"crafting_shaped", (RecipeSerializer)(new a()));


        return serializer;
    }

    public ItemStack getResult() {
        //PeakCraft.getPlugin().getLogger().info("6");
        return this.result;
    }

    public NonNullList<RecipeItemStack> a() {
        //PeakCraft.getPlugin().getLogger().info("7");

        NonNullList<RecipeItemStack> items = NonNullList.a(this.items.size(), RecipeItemStack.a);

        for (int i = 0; i < this.items.size(); i++){
            items.set(i, this.items.get(i).toRecipeItemStack());
        }

        return items;
    }

    public boolean a(InventoryCrafting inventorycrafting, World world) {
        //PeakCraft.getPlugin().getLogger().info("8");
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
        //PeakCraft.getPlugin().getLogger().info("9");
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
        //PeakCraft.getPlugin().getLogger().info("10");
        return this.getResult().cloneItemStack();
    }

    public int i() {
        //PeakCraft.getPlugin().getLogger().info("11");
        return this.width;
    }

    public int j() {
        //PeakCraft.getPlugin().getLogger().info("12");
        return this.height;
    }

    private static NonNullList<RecipeItemChoice> b(String[] astring, Map<String, RecipeItemChoice> map, int i, int j) {
        //PeakCraft.getPlugin().getLogger().info("13");
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
        //PeakCraft.getPlugin().getLogger().info("14");
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
        //PeakCraft.getPlugin().getLogger().info("15");
        int i;
        for(i = 0; i < s.length() && s.charAt(i) == ' '; ++i) {
        }

        return i;
    }

    private static int b(String s) {
        //PeakCraft.getPlugin().getLogger().info("16");
        int i;
        for(i = s.length() - 1; i >= 0 && s.charAt(i) == ' '; --i) {
        }

        return i;
    }

    private static String[] b(JsonArray jsonarray) {
        //PeakCraft.getPlugin().getLogger().info("17");
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
        //PeakCraft.getPlugin().getLogger().info("18");
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
        //PeakCraft.getPlugin().getLogger().info("19");
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
    public static class a implements RecipeSerializer<ShapedNMSRecipe> {
        public a() {
        }

        public ShapedNMSRecipe a(MinecraftKey minecraftkey, JsonObject jsonobject) {
            String s = ChatDeserializer.a(jsonobject, "group", "");
            Map<String, RecipeItemChoice> map = ShapedNMSRecipe.c(ChatDeserializer.t(jsonobject, "key"));
            String[] astring = ShapedNMSRecipe.a(ShapedNMSRecipe.b(ChatDeserializer.u(jsonobject, "pattern")));
            int i = astring[0].length();
            int j = astring.length;
            NonNullList<RecipeItemChoice> nonnulllist = ShapedNMSRecipe.b(astring, map, i, j);
            ItemStack itemstack = ShapedRecipes.a(ChatDeserializer.t(jsonobject, "result"));
            return new ShapedNMSRecipe(minecraftkey, s, i, j, nonnulllist, itemstack);
        }

        public ShapedNMSRecipe a(MinecraftKey minecraftkey, PacketDataSerializer packetdataserializer) {
            int i = packetdataserializer.i();
            int j = packetdataserializer.i();
            String s = packetdataserializer.e(32767);
            NonNullList<RecipeItemChoice> nonnulllist = NonNullList.a(i * j, RecipeItemChoice.a);

            for(int k = 0; k < nonnulllist.size(); ++k) {
                nonnulllist.set(k, RecipeItemChoice.b(packetdataserializer));
            }

            ItemStack itemstack = packetdataserializer.n();
            return new ShapedNMSRecipe(minecraftkey, s, i, j, nonnulllist, itemstack);
        }

        public void a(PacketDataSerializer packetdataserializer, ShapedNMSRecipe shapedrecipes) {
            packetdataserializer.d(shapedrecipes.width);
            packetdataserializer.d(shapedrecipes.height);
            packetdataserializer.a(shapedrecipes.group);
            Iterator iterator = shapedrecipes.items.iterator();

            while(iterator.hasNext()) {
                RecipeItemChoice recipeItemChoice = (RecipeItemChoice)iterator.next();
                recipeItemChoice.a(packetdataserializer);
            }

            packetdataserializer.a(shapedrecipes.result);
        }
    }
}
