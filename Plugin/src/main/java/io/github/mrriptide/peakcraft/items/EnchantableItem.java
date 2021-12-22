package io.github.mrriptide.peakcraft.items;

import io.github.mrriptide.peakcraft.actions.*;
import io.github.mrriptide.peakcraft.exceptions.ItemException;
import io.github.mrriptide.peakcraft.items.enchantments.Enchantment;
import io.github.mrriptide.peakcraft.items.enchantments.EnchantmentData;
import io.github.mrriptide.peakcraft.items.enchantments.EnchantmentManager;
import io.github.mrriptide.peakcraft.recipes.CustomItemStack;
import io.github.mrriptide.peakcraft.util.Attribute;
import io.github.mrriptide.peakcraft.util.PersistentDataManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class EnchantableItem extends Item implements ActionListener {
    protected HashMap<String, Attribute> attributes;
    protected HashMap<String, Enchantment> enchantments;
    protected int maxEnchantmentPoints;
    protected int enchantmentPoints;

    public EnchantableItem(){
        this.attributes = new HashMap<>();
        this.enchantments = new HashMap<>();
        this.maxEnchantmentPoints = 0;
        this.enchantmentPoints = 0;
    }

    public EnchantableItem(Item item){
        super(item);
        if (item instanceof EnchantableItem){
            this.attributes = ((EnchantableItem) item).attributes;
            this.enchantments = ((EnchantableItem) item).enchantments;
            this.maxEnchantmentPoints = ((EnchantableItem) item).maxEnchantmentPoints;
            this.enchantmentPoints = ((EnchantableItem) item).enchantmentPoints;
        } else {
            this.attributes = new HashMap<>();
            this.enchantments = new HashMap<>();
            this.maxEnchantmentPoints = 0;
            this.enchantmentPoints = 0;
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
        this.maxEnchantmentPoints = item.maxEnchantmentPoints;
        this.enchantmentPoints = 0;
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

    public Attribute getAttributeOrDefault(String attributeName, double defaultValue){
        if (attributes == null){
            attributes = new HashMap<>();
        }
        if (!attributes.containsKey(attributeName)){
            attributes.put(attributeName, new Attribute(defaultValue));
        }
        return attributes.getOrDefault(attributeName.toLowerCase(), null);
    }

    public Attribute getAttribute(String attributeName){
        return getAttributeOrDefault(attributeName, 0);
    }

    public boolean canAddEnchant(EnchantmentData enchantmentData, int level){
        return maxEnchantmentPoints >= (enchantmentPoints + enchantmentData.getCost(level)
                - (enchantments.containsKey(enchantmentData.getId()) ? enchantments.get(enchantmentData.getId()).getCost() : 0));
    }

    public void setEnchantmentPoints(int enchantmentPoints){
        this.enchantmentPoints = enchantmentPoints;
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

    public int getMaxEnchantmentPoints(){
        return maxEnchantmentPoints;
    }

    public int getEnchantmentPoints(){
        return enchantmentPoints;
    }

    public void calculateEnchantmentPoints(){
        enchantmentPoints = 0;
        for (Enchantment enchantment : enchantments.values()){
            enchantmentPoints += enchantment.getCost();
        }
    }

    public void addEnchantment(@NotNull String enchantment, int level){
        this.enchantments.put(enchantment.toLowerCase(), EnchantmentManager.getEnchantment(enchantment, level));
        calculateEnchantmentPoints();
    }

    public boolean removeEnchantment(@NotNull String enchantment){
        boolean val = (this.enchantments.remove(enchantment.toLowerCase()) != null);
        calculateEnchantmentPoints();
        return val;
    }

    public static Item loadFromResultSet(Connection conn, ResultSet resultSet) throws SQLException {
        return loadFromResultSet(conn, resultSet, new EnchantableItem());
    }

    public static Item loadFromResultSet(Connection conn, ResultSet resultSet, Item item) throws SQLException {
        EnchantableItem newItem = (EnchantableItem) Item.loadFromResultSet(conn, resultSet, item);

        PreparedStatement statement = conn.prepareStatement("""
SELECT * FROM item_attributes WHERE item_id = ? AND attribute_id = 'max_enchantment_points';
""");
        statement.setString(1, newItem.id);

        ResultSet attributeResultSet = statement.executeQuery();

        newItem.maxEnchantmentPoints = attributeResultSet.next() ? attributeResultSet.getInt("value") : 0;


        attributeResultSet.close();
        statement.close();

        return item;
    }

    @Override
    public void updateItemStack(CustomItemStack itemStack) {
        super.updateItemStack(itemStack);

        ItemMeta meta = itemStack.getItemMeta();

        // Apply enchant glint if it is enchanted
        if (enchantments.size() > 0){
            meta.addEnchant(org.bukkit.enchantments.Enchantment.DAMAGE_ALL, 1, true);
        } else {
            meta.removeEnchant(org.bukkit.enchantments.Enchantment.DAMAGE_ALL);
        }

        PersistentDataContainer container = meta.getPersistentDataContainer();
        for (NamespacedKey key : container.getKeys()){
            if (key.getKey().toLowerCase().startsWith("enchant_")){
                container.remove(key);
            }
        }

        // the enchantments
        for (Enchantment enchantment : enchantments.values()){
            PersistentDataManager.setValue(container, "ENCHANT_" + enchantment.getId().toUpperCase() + "_LEVEL", enchantment.getLevel());
        }

        // the enchant points
        PersistentDataManager.setValue(meta, "max_enchantment_points", maxEnchantmentPoints);
        PersistentDataManager.setValue(meta, "enchantment_points", enchantmentPoints);

        itemStack.setItemMeta(meta);
    }

    @Override
    public Item clone() {
        EnchantableItem clonedItem = new EnchantableItem(super.clone());



        clonedItem.attributes = new HashMap<>();
        clonedItem.attributes.putAll(attributes);
        clonedItem.enchantments = new HashMap<>();
        clonedItem.enchantments.putAll(enchantments);
        clonedItem.maxEnchantmentPoints = this.maxEnchantmentPoints;
        clonedItem.enchantmentPoints = this.enchantmentPoints;


        return clonedItem;
    }

    public HashMap<String, Attribute> getAttributes() {
        return attributes;
    }

    @Override
    public void registerListeners(Action action){
        super.registerListeners(action);
        action.registerListener(this);
        for (Enchantment enchantment : enchantments.values()){
            action.registerListener(enchantment);
        }
    }

    @Override
    public boolean listensTo(Action action) {
        return true;
    }

    @Override
    public void onAction(Action action) {
        if (action instanceof AttackAction) {
            //((AttackAction)action).getDamage().getDamage(Damage.DamageType.GENERIC).addAdditive(attributes.getOrDefault("damage", new Attribute(0)).getFinal());
        } else if (action instanceof DamagedAction) {
            ((DamagedAction)action).getDamage().getProtection(Damage.DamageType.GENERIC).addAdditive(attributes.getOrDefault("defense", new Attribute(0)).getFinal());
        }
    }
}
