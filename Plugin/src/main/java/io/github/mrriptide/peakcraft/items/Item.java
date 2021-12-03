package io.github.mrriptide.peakcraft.items;

import io.github.mrriptide.peakcraft.actions.Action;
import io.github.mrriptide.peakcraft.exceptions.ItemException;
import io.github.mrriptide.peakcraft.items.abilities.Ability;
import io.github.mrriptide.peakcraft.items.abilities.AbilityManager;
import io.github.mrriptide.peakcraft.items.enchantments.Enchantment;
import io.github.mrriptide.peakcraft.recipes.CustomItemStack;
import io.github.mrriptide.peakcraft.util.CustomColors;
import io.github.mrriptide.peakcraft.util.PersistentDataManager;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Item implements Serializable {
    protected String id;
    private String oreDict;
    private String displayName;
    private int rarity;
    private String description;
    private Material material;
    protected String type;
    private ArrayList<Ability> abilities = new ArrayList<>();
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
        this.abilities = new ArrayList<>();
        this.amount = 1;
    }

    public Item(String id) throws ItemException {
        this(Objects.requireNonNull(ItemManager.getItem(id)));
    }

    public Item(Item item){
        this.id = item.id;
        this.oreDict = item.oreDict;
        this.displayName = item.displayName;
        this.rarity = item.rarity;
        this.description = item.description;
        this.material = item.material;
        this.type = item.type;
        this.abilities = item.abilities;
        this.amount = item.amount;
    }

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

    public void updateItemStack(CustomItemStack itemStack){
        itemStack.setType(material);

        //PeakCraft.getPlugin().getLogger().info("Item: " + item);

        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null){
            return;
        }

        // Set the ID

        PersistentDataManager.setValue(meta, "ITEM_ID", id);

        // Add the lore to the item
        meta.setLore(getLore());

        // Set the custom name
        meta.setDisplayName(getRarityColor() + displayName);

        // Makes the item unbreakable
        //meta.setUnbreakable(true);

        // Hide things
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);

        // put metadata on item

        itemStack.setItemMeta(meta);
    }

    public ItemStack convertItem(@NotNull ItemStack item){

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
            // Attributes of item

            HashMap<String, ChatColor> attributeColor = new HashMap<>();
            attributeColor.put("damage", CustomColors.DAMAGE);
            attributeColor.put("defense", CustomColors.DEFENSE);
            attributeColor.put("health", CustomColors.HEALTH);
            if (((EnchantableItem)this).attributes.size() > 0){
                for (String attribute : ((EnchantableItem)this).attributes.keySet()){
                    lore.add(attributeColor.getOrDefault(attribute, CustomColors.ATTRIBUTE) + "" + ChatColor.BOLD + WordUtils.capitalizeFully(attribute) + ChatColor.RESET + CustomColors.ATTRIBUTE_VALUE + ": " + (int)((EnchantableItem)this).getAttribute(attribute).getFinal());
                }

                lore.add("");
            }

            // Enchantments of item

            if (((EnchantableItem)this).enchantments.size() > 0){
                for (Enchantment enchantment : ((EnchantableItem)this).enchantments.values()){
                    lore.add(CustomColors.ENCHANTMENT + enchantment.getDisplayName()
                                    + " " + enchantment.getLevel());
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

        for (Ability ability : abilities) {
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

    public void registerListeners(Action action){
        for (Ability ability : abilities){
            action.registerListener(ability);
        }
    }

    public boolean hasAbility(){
        return abilities.size() > 0;
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
        clonedItem.abilities = (ArrayList<Ability>) this.abilities.clone();
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
SELECT oredict_id from item_oredicts where item_id = ?;
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

        while (abilityResultSet.next()){
            String ability_id = abilityResultSet.getString("ability_id");
            if (AbilityManager.validateAbility(ability_id)){
                item.abilities.add(AbilityManager.getAbility(ability_id));
            }
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
