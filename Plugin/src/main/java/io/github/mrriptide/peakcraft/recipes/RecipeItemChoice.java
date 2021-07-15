//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package io.github.mrriptide.peakcraft.recipes;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import io.github.mrriptide.peakcraft.PeakCraft;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.ints.IntList;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class RecipeItemChoice implements Predicate<ItemStack> {
    //public static final RecipeItemChoice a = new RecipeItemChoice(Stream.empty());
    //private final RecipeItemChoice.Provider[] b;
    private final RecipeItem recipeItem;
    public ItemStack[] choices;
    private IntList d;
    public boolean exact;

    /*public RecipeItemChoice(Stream<? extends RecipeItemChoice.Provider> stream) {
        PeakCraft.getPlugin().getLogger().info("AAAA");
        this.b = (RecipeItemChoice.Provider[])stream.toArray((i) -> {
            return new RecipeItemChoice.Provider[i];
        });
        this.recipeItem = null;
    }*/

    public RecipeItemChoice(RecipeItem recipeItem){
        //this.b = null;
        this.recipeItem = recipeItem;
    }

    /**
     * Evaluates this predicate on the given argument.
     *
     * @param itemStack the input argument
     * @return {@code true} if the input argument matches the predicate,
     * otherwise {@code false}
     */
    @Override
    public boolean test(ItemStack itemStack) {
        return false;
    }

    /*public void buildChoices() {
        if (this.choices == null) {
            if (this.b != null){
                this.choices = (ItemStack[])Arrays.stream(this.b).flatMap((RecipeItemChoice_provider) -> {
                    return RecipeItemChoice_provider.a().stream();
                }).distinct().toArray((i) -> {
                    return new ItemStack[i];
                });
            } else if (this.recipeItem != null){
                this.choices = new ItemStack[]{CraftItemStack.asNMSCopy(this.recipeItem.getItemStack())};
            } else {
                this.choices = new ItemStack[]{CraftItemStack.asNMSCopy((new RecipeItem("air").getItemStack()))};
            }
        }

    }

    public boolean test(@Nullable ItemStack itemstack) {
        if (itemstack == null) {
            return false;
        } else {
            this.buildChoices();
            if (this.choices.length == 0) {
                return itemstack.isEmpty();
            } else {
                ItemStack[] aitemstack = this.choices;
                int i = aitemstack.length;

                for(int j = 0; j < i; ++j) {
                    ItemStack itemstack1 = aitemstack[j];
                    if (this.exact) {
                        if (itemstack1.getItem() == itemstack.getItem() && itemstack.equals(itemstack1)) {
                            return true;
                        }
                    } else if (itemstack1.getItem() == itemstack.getItem()) {
                        return true;
                    }
                }

                return false;
            }
        }
    }

    public RecipeItemStack toRecipeItemStack(){
        RecipeItemStack stack = RecipeItemStack.a;

        stack.choices = choices;

        return stack;
    }

    public IntList b() {
        if (this.d == null) {
            this.buildChoices();
            this.d = new IntArrayList(this.choices.length);
            ItemStack[] aitemstack = this.choices;
            int i = aitemstack.length;

            for(int j = 0; j < i; ++j) {
                ItemStack itemstack = aitemstack[j];
                this.d.add(AutoRecipeStackManager.c(itemstack));
            }

            this.d.sort(IntComparators.NATURAL_COMPARATOR);
        }

        return this.d;
    }

    /**
     * Evaluates this predicate on the given argument.
     *
     * @param itemStack the input argument
     * @return {@code true} if the input argument matches the predicate,
     * otherwise {@code false}

    @Override
    public boolean test(ItemStack itemStack) {
        return false;
    }

    public void a(PacketDataSerializer packetdataserializer) {
        this.buildChoices();
        packetdataserializer.d(this.choices.length);

        for(int i = 0; i < this.choices.length; ++i) {
            packetdataserializer.a(this.choices[i]);
        }

    }

    public JsonElement c() {
        if (this.b.length == 1) {
            return this.b[0].b();
        } else {
            JsonArray jsonarray = new JsonArray();
            RecipeItemChoice.Provider[] aRecipeItemChoice_provider = this.b;
            int i = aRecipeItemChoice_provider.length;

            for(int j = 0; j < i; ++j) {
                RecipeItemChoice.Provider RecipeItemChoice_provider = aRecipeItemChoice_provider[j];
                jsonarray.add(RecipeItemChoice_provider.b());
            }

            return jsonarray;
        }
    }

    public boolean d() {
        return this.b.length == 0 && (this.choices == null || this.choices.length == 0) && (this.d == null || this.d.isEmpty());
    }

    private static RecipeItemChoice b(Stream<? extends RecipeItemChoice.Provider> stream) {
        RecipeItemChoice RecipeItemChoice = new RecipeItemChoice(stream);
        return RecipeItemChoice.b.length == 0 ? a : RecipeItemChoice;
    }

    public static RecipeItemChoice a(IMaterial... aimaterial) {
        return a(Arrays.stream(aimaterial).map(ItemStack::new));
    }

    public static RecipeItemChoice a(Stream<ItemStack> stream) {
        return b(stream.filter((itemstack) -> {
            return !itemstack.isEmpty();
        }).map((itemstack) -> {
            return new RecipeItemChoice.StackProvider(itemstack);
        }));
    }

    public static RecipeItemChoice a(Tag<Item> tag) {
        return b(Stream.of(new RecipeItemChoice.b(tag)));
    }

    public static RecipeItemChoice b(PacketDataSerializer packetdataserializer) {
        int i = packetdataserializer.i();
        return b(Stream.generate(() -> {
            return new RecipeItemChoice.StackProvider(packetdataserializer.n());
        }).limit((long)i));
    }

    public static RecipeItemChoice a(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            if (jsonelement.isJsonObject()) {
                return b(Stream.of(a(jsonelement.getAsJsonObject())));
            } else if (jsonelement.isJsonArray()) {
                JsonArray jsonarray = jsonelement.getAsJsonArray();
                if (jsonarray.size() == 0) {
                    throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");
                } else {
                    return b(StreamSupport.stream(jsonarray.spliterator(), false).map((jsonelement1) -> {
                        return a(ChatDeserializer.m(jsonelement1, "item"));
                    }));
                }
            } else {
                throw new JsonSyntaxException("Expected item to be object or array of objects");
            }
        } else {
            throw new JsonSyntaxException("Item cannot be null");
        }
    }

    private static RecipeItemChoice.Provider a(JsonObject jsonobject) {
        if (jsonobject.has("item") && jsonobject.has("tag")) {
            throw new JsonParseException("An ingredient entry is either a tag or an item, not both");
        } else {
            MinecraftKey minecraftkey;
            if (jsonobject.has("item")) {
                minecraftkey = new MinecraftKey(ChatDeserializer.h(jsonobject, "item"));
                Item item = (Item)IRegistry.ITEM.getOptional(minecraftkey).orElseThrow(() -> {
                    return new JsonSyntaxException("Unknown item '" + minecraftkey + "'");
                });
                return new RecipeItemChoice.StackProvider(new ItemStack(item));
            } else if (jsonobject.has("tag")) {
                minecraftkey = new MinecraftKey(ChatDeserializer.h(jsonobject, "tag"));
                Tag<Item> tag = TagsInstance.a().getItemTags().a(minecraftkey);
                if (tag == null) {
                    throw new JsonSyntaxException("Unknown item tag '" + minecraftkey + "'");
                } else {
                    return new RecipeItemChoice.b(tag);
                }
            } else {
                throw new JsonParseException("An ingredient entry needs either a tag or an item");
            }
        }
    }


    public interface Provider {
        Collection<ItemStack> a();

        JsonObject b();
    }

    public static class StackProvider implements RecipeItemChoice.Provider {
        private final ItemStack a;

        public StackProvider(ItemStack itemstack) {
            this.a = itemstack;
        }

        public Collection<ItemStack> a() {
            return Collections.singleton(this.a);
        }

        public JsonObject b() {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("item", IRegistry.ITEM.getKey(this.a.getItem()).toString());
            return jsonobject;
        }
    }

    static class b implements RecipeItemChoice.Provider {
        private final Tag<Item> a;

        private b(Tag<Item> tag) {
            this.a = tag;
        }

        public Collection<ItemStack> a() {
            List<ItemStack> list = Lists.newArrayList();
            Iterator iterator = this.a.getTagged().iterator();

            while(iterator.hasNext()) {
                Item item = (Item)iterator.next();
                list.add(new ItemStack(item));
            }

            return list;
        }

        public JsonObject b() {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("tag", TagsInstance.a().getItemTags().b(this.a).toString());
            return jsonobject;
        }
    }*/
}
