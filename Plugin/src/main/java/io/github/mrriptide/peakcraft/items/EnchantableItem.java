package io.github.mrriptide.peakcraft.items;

import io.github.mrriptide.peakcraft.actions.Action;
import io.github.mrriptide.peakcraft.exceptions.ItemException;
import io.github.mrriptide.peakcraft.items.enchantments.Enchantment;
import io.github.mrriptide.peakcraft.items.enchantments.EnchantmentManager;
import io.github.mrriptide.peakcraft.recipes.CustomItemStack;
import io.github.mrriptide.peakcraft.util.Attribute;
import io.github.mrriptide.peakcraft.util.PersistentDataManager;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class EnchantableItem extends Item {
    protected HashMap<String, Attribute> attributes;
    protected HashMap<String, Enchantment> enchantments;

    public EnchantableItem(){
        this.attributes = new HashMap<>();
        this.enchantments = new HashMap<>();
    }

    public EnchantableItem(Item item){
        super(item);
        if (item instanceof EnchantableItem){
            this.attributes = ((EnchantableItem) item).attributes;
            this.enchantments = ((EnchantableItem) item).enchantments;
        } else {
            this.attributes = new HashMap<>();
            this.enchantments = new HashMap<>();
        }
    }

    public EnchantableItem(String id, String oreDict, String displayName, int rarity, String description, Material material, String type, HashMap<String, Attribute> attributes, HashMap<String, Enchantment> enchantments){
        super(id, oreDict, displayName, rarity, description, material, type);

        this.attributes = attributes;
        this.enchantments = enchantments;
    }

    public EnchantableItem(String id) throws ItemException {
        super(id);
        EnchantableItem item = null;
        try {
            item = new EnchantableItem(ItemManager.getItem(id));
        } catch (ItemException e) {
            e.printStackTrace();
        }

        this.attributes = item.attributes;
        this.enchantments = new HashMap<>();
    }

    public EnchantableItem(String id, String oreDict, String displayName, int rarity, String description, Material material, String type, HashMap<String, Attribute> attributes){
        this(
                id,
                oreDict,
                displayName,
                rarity,
                description,
                material,
                type,
                attributes,
                new HashMap<>()
        );
    }

    /*public EnchantableItem(ItemStack itemSource) throws IllegalArgumentException{
        super(itemSource);
        // Get ID of the item from the ItemStack

        // Default option
        this.id = PersistentDataManager.getValueOrDefault(itemSource, PersistentDataType.STRING, "ITEM_ID", itemSource.getType().name());

        assert this.id != null;
        Item tmp = ItemManager.getItem(this.id);
        if (tmp instanceof EnchantableItem){
            EnchantableItem default_item = (EnchantableItem) tmp;

            this.attributes = default_item.attributes;
            this.enchantments = new HashMap<>();
            // register enchants
            for (NamespacedKey key : Objects.requireNonNull(itemSource.getItemMeta()).getPersistentDataContainer().getKeys()){
                if (key.getKey().startsWith("enchant_")){
                    addEnchantment(key.getKey().substring(8, key.getKey().length() - 6), PersistentDataManager.getValueOrDefault(itemSource, PersistentDataType.INTEGER, key.getKey(), 0));
                }
            }

            bakeAttributes();
        } else{
            throw new IllegalArgumentException("The item provided is not enchantable");
        }
    }*/

    @Override
    public void registerListeners(Action action){
        super.registerListeners(action);
        for (Enchantment enchantment : enchantments.values()){
            action.registerListener(enchantment);
        }
    }

    public Attribute getAttribute(String attributeName){
        if (attributes == null){
            attributes = new HashMap<>();
        }
        if (!attributes.containsKey(attributeName)){
            attributes.put(attributeName, new Attribute(0));
        }
        return attributes.getOrDefault(attributeName.toLowerCase(), null);
    }

    public void setAttribute(String attributeName, double value){
        if (attributes == null){
            attributes = new HashMap<>();
        }
        attributes.put(attributeName.toLowerCase(), new Attribute(value));
    }

    public HashMap<String, Enchantment> getEnchants(){
        return enchantments;
    }

    public void addEnchantment(@NotNull String enchantment, int level){
        this.enchantments.put(enchantment.toLowerCase(), EnchantmentManager.getEnchantment(enchantment, level));
    }

    public boolean removeEnchantment(@NotNull String enchantment){
        return (this.enchantments.remove(enchantment.toLowerCase()) != null);
    }

    @Override
    public void updateItemStack(CustomItemStack itemStack) {
        super.updateItemStack(itemStack);

        ItemMeta meta = itemStack.getItemMeta();

        // Apply enchant glint if it is enchanted
        if (enchantments.size() > 0){
            meta.addEnchant(org.bukkit.enchantments.Enchantment.DAMAGE_ALL, 1, true);
        }

        // the item id
        PersistentDataManager.setValue(meta, "ITEM_ID", id);
        // the enchantments
        for (Enchantment enchantment : enchantments.values()){
            PersistentDataManager.setValue(meta, "ENCHANT_" + enchantment.getId().toUpperCase() + "_LEVEL", enchantment.getLevel());
        }

        itemStack.setItemMeta(meta);
    }

    @Override
    public Item clone() {
        EnchantableItem clonedItem = new EnchantableItem(super.clone());



        clonedItem.attributes = new HashMap<>();
        clonedItem.attributes.putAll(attributes);
        clonedItem.enchantments = new HashMap<>();
        clonedItem.enchantments.putAll(enchantments);


        return clonedItem;
    }

    public HashMap<String, Attribute> getAttributes() {
        return attributes;
    }
}
