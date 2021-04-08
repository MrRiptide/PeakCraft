package io.github.mrriptide.peakcraft.recipes;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.server.v1_16_R3.*;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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

    public void a(PacketDataSerializer packetDataSerializer) {
        packetDataSerializer.d(this.choices.size());

        for(int i = 0; i < this.choices.size(); ++i) {
            packetDataSerializer.a(CraftItemStack.asNMSCopy(this.choices.get(i).getItemStack()));
        }
    }

    /*private static RecipeItemChoice b(Stream<? extends RecipeItemChoice.Provider> stream) {
        RecipeItemChoice recipeitemstack = new RecipeItemChoice(stream);
        return recipeitemstack.b.length == 0 ? new RecipeItemChoice("air") : recipeitemstack;
    }*/

    // I might decide to rewrite this, I think this is to convert a json element to object?? idk ill try to figure it out
    public static RecipeItemChoice a(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            if (jsonelement.isJsonObject()) {
                //jsonelement.getAsJsonObject().get();
                RecipeItemChoice recipeItemChoice = new RecipeItemChoice("air");
                throw new NotImplementedException();
                //return b(Stream.of(a(jsonelement.getAsJsonObject())));
            } else if (jsonelement.isJsonArray()) {
                JsonArray jsonarray = jsonelement.getAsJsonArray();
                if (jsonarray.size() == 0) {
                    throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");
                } else {
                    throw new NotImplementedException();
                    /*return b(StreamSupport.stream(jsonarray.spliterator(), false).map((jsonelement1) -> {
                        return a(ChatDeserializer.m(jsonelement1, "item"));
                    }));*/
                }
            } else {
                throw new JsonSyntaxException("Expected item to be object or array of objects");
            }
        } else {
            throw new JsonSyntaxException("Item cannot be null");
        }
    }

    public interface Provider {
        Collection<net.minecraft.server.v1_16_R3.ItemStack> a();

        JsonObject b();
    }

    static class b implements RecipeItemStack.Provider {
        private final Tag<Item> a;

        private b(Tag<Item> tag) {
            this.a = tag;
        }

        public Collection<net.minecraft.server.v1_16_R3.ItemStack> a() {
            List<net.minecraft.server.v1_16_R3.ItemStack> list = Lists.newArrayList();
            Iterator iterator = this.a.getTagged().iterator();

            while(iterator.hasNext()) {
                Item item = (Item)iterator.next();
                list.add(new net.minecraft.server.v1_16_R3.ItemStack(item));
            }

            return list;
        }

        public JsonObject b() {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("tag", TagsInstance.a().getItemTags().b(this.a).toString());
            return jsonobject;
        }
    }
}
