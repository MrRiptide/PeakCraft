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

import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftNamespacedKey;

public class ShapedNMSRecipe extends NMSCraftingRecipe {
    private final int width;
    private final int height;
    private final NonNullList<RecipeItem> items;
    private final String group;

    public ShapedNMSRecipe(ResourceLocation resourceLocation, String s, int i, int j, NonNullList<RecipeItem> nonnulllist, RecipeItem resultRecipeItem) {
        //PeakCraft.getPlugin().getLogger().info("1");
        this.setId(resourceLocation);
        this.group = s;
        this.width = i;
        this.height = j;
        this.items = nonnulllist;
        this.setResult(resultRecipeItem);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        for(int i = 0; i <= container.getWidth() - this.width; ++i) {
            for(int j = 0; j <= container.getHeight() - this.height; ++j) {
                if (this.matches(container, i, j, true)) {
                    return true;
                }

                if (this.matches(container, i, j, false)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public ItemStack assemble(CraftingContainer craftingContainer) {
        return CraftItemStack.asNMSCopy(getResult().getItemStack());
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.register("crafting_shaped", new ShapedNMSRecipe.Serializer());
    }

    @Override
    public RecipeType<?> getType() {
        return RecipeType.CRAFTING;
    }

    private boolean matches(CraftingContainer container, int i, int j, boolean flag) {
        for(int k = 0; k < container.getWidth(); ++k) {
            for(int l = 0; l < container.getHeight(); ++l) {
                int i1 = k - i;
                int j1 = l - j;
                RecipeItem recipeitemstack = null;
                if (i1 >= 0 && j1 >= 0 && i1 < this.width && j1 < this.height) {
                    if (flag) {
                        recipeitemstack = this.items.get(this.width - i1 - 1 + j1 * this.width);
                    } else {
                        recipeitemstack = this.items.get(i1 + j1 * this.width);
                    }
                }

                if (recipeitemstack != null && !recipeitemstack.test(new RecipeItem(CraftItemStack.asBukkitCopy(container.getItem(k + l * container.getWidth()))))) {
                    return false;
                }
            }
        }

        return true;
    }

    public org.bukkit.inventory.Recipe toBukkitRecipe() {
        // Make RecipeItem array
        RecipeItem[][] ingredientArray = new RecipeItem[height][width];

        for (int i = 0; i < height; i++){
            for (int j = 0; j < width; j++){
                ingredientArray[i][j] = items.get(i*3 + j);
            }
        }

        // Get RecipeItem result

        RecipeItem result = getResult();

        ShapedRecipe recipe = new ShapedRecipe(ingredientArray, result);

        recipe.setGroup(this.group);
        recipe.setKey(CraftNamespacedKey.fromMinecraft(this.getId()));

        return recipe;
    }

    static Map<String, Ingredient> keyFromJson(JsonObject jsonobject) {
        Map<String, Ingredient> map = Maps.newHashMap();
        Iterator iterator = jsonobject.entrySet().iterator();

        while(iterator.hasNext()) {
            Entry<String, JsonElement> entry = (Entry)iterator.next();
            if (((String)entry.getKey()).length() != 1) {
                throw new JsonSyntaxException("Invalid key entry: '" + (String)entry.getKey() + "' is an invalid symbol (must be 1 character only).");
            }

            if (" ".equals(entry.getKey())) {
                throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
            }

            map.put((String)entry.getKey(), Ingredient.fromJson((JsonElement)entry.getValue()));
        }

        map.put(" ", Ingredient.EMPTY);
        return map;
    }

    private static int firstNonSpace(String s) {
        int i;
        for(i = 0; i < s.length() && s.charAt(i) == ' '; ++i) {
        }

        return i;
    }

    private static int lastNonSpace(String s) {
        int i;
        for(i = s.length() - 1; i >= 0 && s.charAt(i) == ' '; --i) {
        }

        return i;
    }

    @VisibleForTesting
    static String[] shrink(String... astring) {
        int i = 2147483647;
        int j = 0;
        int k = 0;
        int l = 0;

        for(int i1 = 0; i1 < astring.length; ++i1) {
            String s = astring[i1];
            i = Math.min(i, firstNonSpace(s));
            int j1 = lastNonSpace(s);
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
    public static ItemStack itemStackFromJson(JsonObject jsonobject) {
        Item item = itemFromJson(jsonobject);
        if (jsonobject.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
        } else {
            int i = GsonHelper.getAsInt(jsonobject, "count", 1);
            if (i < 1) {
                throw new JsonSyntaxException("Invalid output count: " + i);
            } else {
                return new ItemStack(item, i);
            }
        }
    }

    public static Item itemFromJson(JsonObject jsonobject) {
        String s = GsonHelper.getAsString(jsonobject, "item");
        Item item = (Item) Registry.ITEM.getOptional(new ResourceLocation(s)).orElseThrow(() -> {
            return new JsonSyntaxException("Unknown item '" + s + "'");
        });
        if (item == Items.AIR) {
            throw new JsonSyntaxException("Invalid item: " + s);
        } else {
            return item;
        }
    }

    static String[] patternFromJson(JsonArray jsonarray) {
        String[] astring = new String[jsonarray.size()];
        if (astring.length > 3) {
            throw new JsonSyntaxException("Invalid pattern: too many rows, 3 is maximum");
        } else if (astring.length == 0) {
            throw new JsonSyntaxException("Invalid pattern: empty pattern not allowed");
        } else {
            for(int i = 0; i < astring.length; ++i) {
                String s = GsonHelper.convertToString(jsonarray.get(i), "pattern[" + i + "]");
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

    static NonNullList<Ingredient> dissolvePattern(String[] astring, Map<String, Ingredient> map, int i, int j) {
        NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i * j, Ingredient.EMPTY);
        Set<String> set = Sets.newHashSet(map.keySet());
        set.remove(" ");

        for(int k = 0; k < astring.length; ++k) {
            for(int l = 0; l < astring[k].length(); ++l) {
                String s = astring[k].substring(l, l + 1);
                Ingredient recipeitemstack = (Ingredient)map.get(s);
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

    public static class Serializer implements RecipeSerializer<ShapedNMSRecipe> {
        public Serializer() {
        }

        public ShapedNMSRecipe fromJson(ResourceLocation minecraftkey, JsonObject jsonobject) {
            String s = GsonHelper.getAsString(jsonobject, "group", "");
            Map<String, Ingredient> map = ShapedNMSRecipe.keyFromJson(GsonHelper.getAsJsonObject(jsonobject, "key"));
            String[] astring = ShapedNMSRecipe.shrink(ShapedNMSRecipe.patternFromJson(GsonHelper.getAsJsonArray(jsonobject, "pattern")));
            int i = astring[0].length();
            int j = astring.length;
            NonNullList<RecipeItem> nonnulllist = NonNullList.create();
            for (Ingredient ingredient : ShapedNMSRecipe.dissolvePattern(astring, map, i, j)){
                nonnulllist.add(new RecipeItem(CraftItemStack.asBukkitCopy(ingredient.itemStacks[0])));
            }

            ItemStack itemstack = ShapedNMSRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(jsonobject, "result"));
            return new ShapedNMSRecipe(minecraftkey, s, i, j, nonnulllist, new RecipeItem(CraftItemStack.asBukkitCopy(itemstack)));
        }

        public ShapedNMSRecipe fromNetwork(ResourceLocation minecraftkey, FriendlyByteBuf packetdataserializer) {
            int i = packetdataserializer.readVarInt();
            int j = packetdataserializer.readVarInt();
            String s = packetdataserializer.readUtf();
            NonNullList<RecipeItem> nonnulllist = NonNullList.withSize(i * j, new RecipeItem());

            for(int k = 0; k < nonnulllist.size(); ++k) {
                nonnulllist.set(k, new RecipeItem(CraftItemStack.asBukkitCopy(Ingredient.fromNetwork(packetdataserializer).itemStacks[0])));
            }

            ItemStack itemstack = packetdataserializer.readItem();
            return new ShapedNMSRecipe(minecraftkey, s, i, j, nonnulllist, new RecipeItem(CraftItemStack.asBukkitCopy(itemstack)));
        }

        public void toNetwork(FriendlyByteBuf packetdataserializer, ShapedNMSRecipe shapedrecipes) {
            packetdataserializer.writeVarInt(shapedrecipes.width);
            packetdataserializer.writeVarInt(shapedrecipes.height);
            packetdataserializer.writeUtf(shapedrecipes.group);
            Iterator iterator = shapedrecipes.items.iterator();

            while(iterator.hasNext()) {
                RecipeItem recipeitemstack = (RecipeItem)iterator.next();
                recipeitemstack.toNetwork(packetdataserializer);
            }

            packetdataserializer.writeItem(shapedrecipes.getResultItem());
        }
    }
}
