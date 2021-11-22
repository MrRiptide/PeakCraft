package io.github.mrriptide.peakcraft.items;

import io.github.mrriptide.peakcraft.entity.player.PlayerWrapper;
import io.github.mrriptide.peakcraft.exceptions.ItemException;
import io.github.mrriptide.peakcraft.items.abilities.Ability;
import io.github.mrriptide.peakcraft.items.abilities.AbilityManager;
import io.github.mrriptide.peakcraft.items.abilities.triggers.AbilityTrigger;
import io.github.mrriptide.peakcraft.items.enchantments.EnchantmentManager;
import io.github.mrriptide.peakcraft.util.CustomColors;
import io.github.mrriptide.peakcraft.util.PersistentDataManager;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Item implements Serializable {
    protected String id;
    private String oreDict;
    private String displayName;
    private int rarity;
    private String description;
    private Material material;
    protected String type;
    private Ability ability;
    private int amount;

    public Item(){

    }

    public Item(String id, String oreDict, String displayName, int rarity, String description, Material material, String type){
        this.id = id;
        this.oreDict = oreDict;
        this.displayName = displayName;
        this.rarity = rarity;
        this.description = description;
        this.material = material;
        this.type = (type != null && !type.isEmpty()) ? type : "item";
        this.ability = null;
        this.amount = 1;
    }

    public Item(String id){
        Item item = null;
        try {
            item = ItemManager.getItem(id);
        } catch (ItemException e) {
            e.printStackTrace();
        }

        assert item != null;
        this.id = item.id;
        this.oreDict = item.oreDict;
        this.displayName = item.displayName;
        this.rarity = item.rarity;
        this.description = item.description;
        this.material = item.material;
        this.type = item.type;
        this.ability = item.ability;
        this.amount = item.amount;
    }

    public Item(Item item){
        this.id = item.id;
        this.oreDict = item.oreDict;
        this.displayName = item.displayName;
        this.rarity = item.rarity;
        this.description = item.description;
        this.material = item.material;
        this.type = item.type;
        this.ability = item.ability;
        this.amount = item.amount;
    }

    /*public Item(ItemStack itemSource){
        // Get ID of the item from the ItemStack

        // Default option
        this.id = PersistentDataManager.getValueOrDefault(itemSource, PersistentDataType.STRING, "ITEM_ID", itemSource.getType().name());

        assert this.id != null;
        Item default_item = ItemManager.getItem(this.id);

        this.oreDict = default_item.oreDict;
        this.rarity = default_item.rarity;
        this.displayName = default_item.displayName;
        this.description = default_item.description;
        this.type = default_item.type;
        this.material = default_item.material;
        this.ability = default_item.ability;
    }*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(id, item.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public ItemStack getItemStack(){
        ItemStack item = new ItemStack(material);

        //PeakCraft.getPlugin().getLogger().info("Item: " + item);

        ItemMeta meta = item.getItemMeta();
        if (meta == null){
            return item;
        }

        // Set the ID

        PersistentDataManager.setValue(meta, "ITEM_ID", id);

        // Add the lore to the item
        meta.setLore(getLore());

        // Set the custom name
        meta.setDisplayName(getRarityColor() + displayName);

        // Makes the item unbreakable
        meta.setUnbreakable(true);

        // Hide things
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

        // put metadata on item

        item.setItemMeta(meta);

        return item;
    }

    public ItemStack convertItem(ItemStack item){

        ItemMeta meta = item.getItemMeta();

        // Add the lore to the item
        assert meta != null;
        meta.setLore(getLore());

        // Set the custom name
        meta.setDisplayName(getFormattedDisplayName());

        item.setItemMeta(meta);

        return item;
    }

    public String getFormattedDisplayName(){
        return getRarityColor() + displayName;
    }

    public ArrayList<String> getLore(){
        ArrayList<String> lore = new ArrayList<>();

        if (this instanceof EnchantableItem){
            ((EnchantableItem)this).bakeAttributes();
            // Attributes of item

            HashMap<String, ChatColor> attributeColor = new HashMap<>();
            attributeColor.put("damage", CustomColors.DAMAGE);
            attributeColor.put("defense", CustomColors.DEFENSE);
            attributeColor.put("health", CustomColors.HEALTH);
            if (((EnchantableItem)this).attributes.size() > 0){
                for (String attribute : ((EnchantableItem)this).attributes.keySet()){
                    lore.add(attributeColor.getOrDefault(attribute, CustomColors.ATTRIBUTE) + "" + ChatColor.BOLD + WordUtils.capitalizeFully(attribute) + ChatColor.RESET + CustomColors.ATTRIBUTE_VALUE + ": " + (int)((EnchantableItem)this).getAttribute(attribute));
                }

                lore.add("");
            }

            // Enchantments of item

            if (((EnchantableItem)this).enchantments.size() > 0){
                for (Map.Entry<String, Integer> enchantment : ((EnchantableItem)this).enchantments.entrySet()){
                    lore.add(CustomColors.ENCHANTMENT +
                            ((EnchantmentManager.validateEnchantment(enchantment.getKey()) ?
                                    EnchantmentManager.getEnchantment(enchantment.getKey()).getDisplayName() :
                                    "Unknown Enchantment")
                                    + " " + enchantment.getValue()));
                }

                lore.add("");
            }
        }

        // Description of item
        if (description != null && description.length() > 0){
            String[] wrapped_description = WordUtils.wrap(description, 30, "\n", true).split("\n");
            for (String line : wrapped_description){
                lore.add("§7" + line);
            }
            lore.add("");
        }

        if (ability != null){
            lore.addAll(ability.getLore());
            lore.add("");
        }

        if (this instanceof ArmorItem && ((ArmorItem)this).getSet() != null){
            lore.addAll(((ArmorItem)this).getSet().getLore());
            lore.add("");
        }

        // Rarity of item
        lore.add(getRarityColor() + getRarityName().toUpperCase() + " " + type.toUpperCase());

        return lore;
    }

    public boolean hasAbility(){
        return ability != null;
    }

    public void useAbility(PlayerWrapper player, AbilityTrigger trigger){
        if (ability != null){
            AbilityManager.triggerAbility(ability, player, trigger);
        }
    }

    public Ability getAbility(){
        return this.ability;
    }

    protected ChatColor getRarityColor(){
        ChatColor[] colors = {
                CustomColors.ERROR, // Broken
                CustomColors.COMMON, // Common
                CustomColors.UNCOMMON, // Uncommon
                CustomColors.RARE, // Rare
                CustomColors.EPIC, // Epic
                CustomColors.LEGENDARY, // Legendary
                CustomColors.MYTHIC, // Mythic
                CustomColors.RELIC}; // Relic
        return colors[rarity];
    }

    protected String getRarityName() {
        String[] names = {"Broken", "Common", "Uncommon", "Rare", "Epic", "Legendary", "Mythic", "Relic"};

        return ChatColor.BOLD + names[rarity];
    }

    public Item clone() {
        Item clonedItem = new Item();

        clonedItem.id = this.id;
        clonedItem.oreDict = this.oreDict;
        clonedItem.displayName = this.displayName;
        clonedItem.rarity = this.rarity;
        clonedItem.description = this.description;
        clonedItem.material = this.material;
        clonedItem.type = this.type;
        clonedItem.ability = this.ability;
        return clonedItem;
    }

    public static Item loadFromResultSet(Connection conn, ResultSet resultSet) throws SQLException {
        return loadFromResultSet(conn, resultSet, new Item());
    }

    public static Item loadFromResultSet(Connection conn, ResultSet resultSet, Item item) throws SQLException {
        item.id = resultSet.getString("id");
        //item.oreDict = resultSet.getString("oreDict");
        item.displayName = resultSet.getString("display_name");
        item.rarity = resultSet.getInt("rarity");
        item.description = resultSet.getString("description");
        item.material = Material.getMaterial(resultSet.getString("material_id").toUpperCase());
        item.type = resultSet.getString("type");

        PreparedStatement statement = conn.prepareStatement("""
SELECT oredict_id from item_oreDicts where item_id = ?;
""");
        statement.setString(1, item.id);

        ResultSet oredictResultSet = statement.executeQuery();

        if (oredictResultSet.next()){
            item.oreDict = oredictResultSet.getString("oredict_id");
        } else {
            item.oreDict = "";
        }

        oredictResultSet.close();
        statement.close();

        statement = conn.prepareStatement("""
SELECT ability_id from item_abilities where item_id = ?;
""");
        statement.setString(1, item.id);

        ResultSet abilityResultSet = statement.executeQuery();

        if (abilityResultSet.next()){
            String ability_id = abilityResultSet.getString("ability_id");
            if (AbilityManager.validateAbility(ability_id)){
                item.ability = AbilityManager.getAbility(ability_id);
            } else {
                item.ability = null;
            }
        } else {
            item.ability = null;
        }

        abilityResultSet.close();
        statement.close();

        return item;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOreDict() {
        return oreDict;
    }

    public void setOreDict(String oreDict) {
        this.oreDict = oreDict;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getRarity() {
        return rarity;
    }

    public void setRarity(int rarity) {
        this.rarity = rarity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public String getMaterialStr() {
        return material.name();
    }

    public void setMaterialFromStr(String materialName) {
        this.material = Material.getMaterial(materialName);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAmount(int amount){
        this.amount = amount;
    }
}
