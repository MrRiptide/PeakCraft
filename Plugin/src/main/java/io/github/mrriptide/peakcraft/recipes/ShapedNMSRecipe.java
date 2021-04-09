package io.github.mrriptide.peakcraft.recipes;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.github.mrriptide.peakcraft.PeakCraft;
import net.minecraft.server.v1_16_R3.*;
import org.apache.commons.lang.NotImplementedException;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftNamespacedKey;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ShapedNMSRecipe extends NMSRecipe {
    private HashMap<Character, RecipeItemChoice> ingredientMap;
    private String[] shape;
    private int width;
    private int height;

    public ShapedNMSRecipe(MinecraftKey minecraftkey, String group, int width, int height, NonNullList<RecipeItemChoice> nonnulllist, ItemStack resultItemStack) {
        this.setKey(minecraftkey);
        this.setGroup(group);
        // idk what to do with any of this data but i feel like i should do something???
        //this.width = width;
        //this.height = height;
        //this.items = nonnulllist;
        this.setResult(new RecipeItem(CraftItemStack.asBukkitCopy(resultItemStack)));
    }

    public ShapedNMSRecipe(ShapedRecipe sourceRecipe){
        this.setResult(sourceRecipe.getResult());
        this.setGroup(sourceRecipe.getGroup());
        this.shape = sourceRecipe.getShape();
        this.setKey(CraftNamespacedKey.toMinecraft(sourceRecipe.getKey()));

        this.ingredientMap = new HashMap<>();
        for (Map.Entry<Character, RecipeItem> ingredient : sourceRecipe.getIngredientMap().entrySet()){
            this.ingredientMap.put(ingredient.getKey(), new RecipeItemChoice(ingredient.getValue()));
        }
    }

    /**
     *  Returns a boolean based on if the generic recipe matches the specific recipe passed in
     *
     * @TODO: OreDict feature for things like logs or stone
     *
     * @param   recipe  the crafted recipe to compare to
     * @return          if the generic recipe matches the specific recipe
     * */
    @Override
    public boolean test(NMSRecipe recipe){
        ShapedNMSRecipe shapedRecipe = (ShapedNMSRecipe)recipe;

        if (this == recipe || this.equals(recipe)){
            return true;
        }

        if (Arrays.equals(this.shape, shapedRecipe.shape)){
            if (this.ingredientMap.equals(shapedRecipe.ingredientMap)){
                return true;
            } else {
                for (Character key : this.ingredientMap.keySet()){
                    RecipeItemChoice recipeItem = this.ingredientMap.get(key);
                    RecipeItemChoice craftItem = shapedRecipe.ingredientMap.get(key);
                    if (recipeItem.test(craftItem)){
                        return false;
                    }
                }
                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    // THEORY: TESTS TO SEE IF RECIPE MATCHES
    public boolean a(InventoryCrafting inventoryCrafting, World world) {
        PeakCraft.getPlugin().getLogger().info("Called a #1");
        throw new NotImplementedException("a1");
    }

    @Override
    /*
        Gets an instance of the result itemstack, no clue what purpose it holds though
     */
    public ItemStack a(InventoryCrafting inventoryCrafting) {
        PeakCraft.getPlugin().getLogger().info("Called a #2");
        return this.getResult().cloneItemStack();
    }

    public static int iters = 0;
    @Override
    public RecipeSerializer<?> getRecipeSerializer() {
        PeakCraft.getPlugin().getLogger().info("Called getRecipeSerializer(" + ++iters + ")");
        //fails on call number 988?
        PeakCraft.getPlugin().getLogger().info(getKey().getKey());
        if (iters > 987){
            Thread.dumpStack();
        }
        //PacketPlayOutRecipeUpdate
        //ShapedRecipes
        RecipeSerializer<ShapedNMSRecipe> serializer = RecipeSerializer.a((String)"crafting_shaped", (RecipeSerializer)(new ShapedNMSRecipe.a()));
        return serializer;
    }

    @Override
    public Recipes<?> g() {
        return Recipes.CRAFTING;
    }

    @Override
    public org.bukkit.inventory.Recipe toBukkitRecipe() {
        throw new NotImplementedException("toBukkitRecipe");

    }

    // I think it validates the map?
    private static Map<String, RecipeItemChoice> c(JsonObject jsonobject) {
        Map<String, RecipeItemChoice> map = Maps.newHashMap();
        Iterator iterator = jsonobject.entrySet().iterator();

        while(iterator.hasNext()) {
            Map.Entry<String, JsonElement> entry = (Map.Entry)iterator.next();
            if (((String)entry.getKey()).length() != 1) {
                throw new JsonSyntaxException("Invalid key entry: '" + (String)entry.getKey() + "' is an invalid symbol (must be 1 character only).");
            }

            if (" ".equals(entry.getKey())) {
                throw new JsonSyntaxException("Invalid key entry: ' ' is a reserved symbol.");
            }

            map.put((String)entry.getKey(), RecipeItemChoice.a((JsonElement)entry.getValue()));
        }

        map.put(" ", new RecipeItemChoice("air"));
        return map;
    }

    // recipe serializer class for custom shaped recipes
    public static class a implements RecipeSerializer<ShapedNMSRecipe> {
        public a() {
        }

        public ShapedNMSRecipe a(MinecraftKey minecraftkey, JsonObject jsonobject) {
            throw new NotImplementedException();
            /*String s = ChatDeserializer.a(jsonobject, "group", "");
            Map<String, RecipeItemChoice> map = ShapedNMSRecipe.c(ChatDeserializer.t(jsonobject, "key"));
            String[] astring = ShapedNMSRecipe.a(ShapedNMSRecipe.b(ChatDeserializer.u(jsonobject, "pattern")));
            int i = astring[0].length();
            int j = astring.length;
            NonNullList<RecipeItemStack> nonnulllist = ShapedNMSRecipe.b(astring, map, i, j);
            ItemStack itemstack = ShapedNMSRecipe.a(ChatDeserializer.t(jsonobject, "result"));
            return new ShapedNMSRecipe(minecraftkey, s, i, j, nonnulllist, itemstack);*/
        }

        public ShapedNMSRecipe a(MinecraftKey minecraftkey, PacketDataSerializer packetdataserializer) {
            PeakCraft.getPlugin().getLogger().info("        public ShapedNMSRecipe a(MinecraftKey minecraftkey, PacketDataSerializer packetdataserializer)");
            int i = packetdataserializer.i();
            int j = packetdataserializer.i();
            String s = packetdataserializer.e(32767);
            NonNullList<RecipeItemChoice> nonnulllist = NonNullList.a(i * j, new RecipeItemChoice("air"));

            for(int k = 0; k < nonnulllist.size(); ++k) {
                //nonnulllist.set(k, RecipeItemChoice.b(packetdataserializer));
            }

            ItemStack itemstack = packetdataserializer.n();
            return new ShapedNMSRecipe(minecraftkey, s, i, j, nonnulllist, itemstack);
        }

        @Override
        public void a(PacketDataSerializer packetDataSerializer, ShapedNMSRecipe shapedNMSRecipe) {
            PeakCraft.getPlugin().getLogger().info("public void a(PacketDataSerializer packetDataSerializer, ShapedNMSRecipe shapedNMSRecipe)");
            packetDataSerializer.d(shapedNMSRecipe.shape[0].length());
            packetDataSerializer.d(shapedNMSRecipe.shape.length);
            packetDataSerializer.a(shapedNMSRecipe.getGroup());
            Iterator iterator = shapedNMSRecipe.ingredientMap.values().iterator();

            while(iterator.hasNext()) {
                RecipeItemChoice recipeItemChoice = (RecipeItemChoice) iterator.next();
                recipeItemChoice.a(packetDataSerializer);
            }

            packetDataSerializer.a(shapedNMSRecipe.getResult());
        }
    }
}
