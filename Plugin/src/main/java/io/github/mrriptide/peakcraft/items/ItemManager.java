package io.github.mrriptide.peakcraft.items;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.cj.protocol.Resultset;
import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.tsv.TsvParser;
import com.univocity.parsers.tsv.TsvParserSettings;
import com.univocity.parsers.tsv.TsvWriter;
import com.univocity.parsers.tsv.TsvWriterSettings;
import io.github.mrriptide.peakcraft.PeakCraft;
import io.github.mrriptide.peakcraft.exceptions.ItemException;
import io.github.mrriptide.peakcraft.recipes.ShapedRecipe;
import io.github.mrriptide.peakcraft.util.Formatter;
import io.github.mrriptide.peakcraft.util.MySQLHelper;
import io.github.mrriptide.peakcraft.util.PersistentDataManager;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.XMLEncoder;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;

public class ItemManager {

    private static String itemFilePath = "items.json";
    private static HashMap<String, Item> items = new HashMap<>();

    public static void getItemFromItemStack(ItemStack itemStack){
        Item item;

        String type = PersistentDataManager.getValueOrDefault(itemStack, PersistentDataType.STRING, "type", "item");
    }

    public static HashMap<String, Item> getItems(){
        return items;
    }

    public static void saveMaterial(Material material) throws SQLException {
        Connection conn = MySQLHelper.getConnection();
        saveMaterial(conn, material);
        conn.close();
    }

    public static void saveMaterial(Connection conn, Material material) throws SQLException {
        PreparedStatement newStatement = conn.prepareStatement("""
INSERT INTO new_items (id, display_name, description, rarity, material_id, type) VALUES (?,?,?,?,?,?)
""");
        newStatement.setString(1, material.name().toUpperCase(Locale.ROOT));
        newStatement.setString(2, Formatter.humanize(material.name()));
        newStatement.setString(3, "");
        newStatement.setInt(4, 0);
        newStatement.setString(5, material.name());
        newStatement.setString(6, "Item");

        newStatement.execute();
        newStatement.close();
    }

    public static void loadNewItems(){
        // check if the items table exists

        try {
            // load through all spigot items to confirm that they all exist in the database

            Connection conn = MySQLHelper.getConnection();

            int newItems = 0;

            for (Material material : Arrays.asList(Material.values())){
                PreparedStatement statement = conn.prepareStatement("""
SELECT id FROM (SELECT * FROM items UNION SELECT * FROM new_items) as merged_items WHERE id = ? LIMIT 1;
""");
                statement.setString(1, material.name().toUpperCase(Locale.ROOT));
                final ResultSet resultSet = statement.executeQuery();

                if(!resultSet.next()) {
                    newItems += 1;
                    saveMaterial(conn, material);
                }
                resultSet.close();
                statement.close();
            }
            conn.close();

            if (newItems > 0)
                PeakCraft.getPlugin().getLogger().warning(newItems + " new items found. Please update the data in the database and then run /onloaditems");
            else {
                PeakCraft.getPlugin().getLogger().info("No new items found");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void loadItem(String id) throws SQLException {
        Connection conn = MySQLHelper.getConnection();
        PreparedStatement statement = conn.prepareStatement("""
SELECT * FROM (SELECT * FROM items UNION SELECT * FROM new_items) as merged_items WHERE id = ?;
""");
        statement.setString(1, id.toUpperCase());
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()){
            loadItem(conn, resultSet);
        }
        resultSet.close();
        statement.close();
        conn.close();
    }

    public static void loadItem(Connection conn, ResultSet resultSet) throws SQLException {
        String type = resultSet.getString("type");

        if (ArmorItem.validateType(type)){
            // load as armor item
            items.put(resultSet.getString("id").toUpperCase(), ArmorItem.loadFromResultSet(conn, resultSet));
        } else if (WeaponItem.validateType(type)){
            // load as weapon item
            items.put(resultSet.getString("id").toUpperCase(), WeaponItem.loadFromResultSet(conn, resultSet));
        } else {
            // load as normal item
            items.put(resultSet.getString("id").toUpperCase(), Item.loadFromResultSet(conn, resultSet));
        }
    }

    public static void loadItems() {
        try {
            items.clear();

            Connection conn = MySQLHelper.getConnection();
            PreparedStatement statement = conn.prepareStatement("""
SELECT * FROM (SELECT * FROM items UNION SELECT * FROM new_items) as merged_items;
""");

            final ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()){
                loadItem(conn, resultSet);
            }

            PeakCraft.getPlugin().getLogger().info("Loaded " + items.size() + " items");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static Item getItem(String id) throws ItemException {
        if (items.containsKey(id.toUpperCase())){
            return items.get(id.toUpperCase()).clone();
        } else {
            PeakCraft.getPlugin().getLogger().info(items.keySet().toString());
            if (Material.matchMaterial(id) != null){
                try {
                    saveMaterial(Material.matchMaterial(id));
                    loadItem(id);
                    return items.get(id.toUpperCase()).clone();
                } catch (SQLException e) {
                    PeakCraft.getPlugin().getLogger().warning(e.getMessage());
                    return null;
                }
            } else {
                throw new ItemException("An item was requested that doesn't exist in the item database: \"" + id.toUpperCase() + "\"");
            }
        }
    }

    public static Item convertItem(org.bukkit.inventory.ItemStack itemSource) throws ItemException {
        if (itemSource == null || itemSource.getType().equals(Material.AIR)){
            try {
                return getItem("air");
            } catch (ItemException e) {
                PeakCraft.getPlugin().getLogger().warning(e.getMessage());
            }
        }
        String id = PersistentDataManager.getValueOrDefault(itemSource, PersistentDataType.STRING, "ITEM_ID", itemSource.getType().name());

        Item item = getItem(id);

        // Move any general attributes to the item from the itemstack
        item.setAmount(itemSource.getAmount());

        if (item instanceof EnchantableItem){

            ((EnchantableItem)item).enchantments = new HashMap<>();
            // register enchants
            for (NamespacedKey key : Objects.requireNonNull(itemSource.getItemMeta()).getPersistentDataContainer().getKeys()){
                if (key.getKey().startsWith("enchant_")){
                    ((EnchantableItem)item).addEnchantment(key.getKey().substring(8, key.getKey().length() - 6), PersistentDataManager.getValueOrDefault(itemSource, PersistentDataType.INTEGER, key.getKey(), 0));
                }
            }

            ((EnchantableItem)item).bakeAttributes();
        }

        return item;
    }

    private static void createItemList() {
        writeMaterialsToFile(Arrays.asList(Material.values()), itemFilePath);
    }

    public static void writeMaterialsToFile(List<Material> materials, String fileName){
        try {
            if (!PeakCraft.instance.getDataFolder().exists()){
                PeakCraft.instance.getDataFolder().mkdirs();
            }

            HashMap<String, HashMap<String, String>> items = new HashMap<>();

            for (Material mat : materials){
                if (mat.isItem()){
                    HashMap<String, String> map = new HashMap<>();
                    map.put("id", mat.name());
                    map.put("oreDict", "");
                    map.put("description", "");
                    map.put("displayName", Formatter.humanize(mat.toString()));
                    map.put("rarity", "1");
                    map.put("materialID", mat.name());
                    map.put("type", "Item");

                    items.put(mat.name(), map);
                } else {
                    PeakCraft.getPlugin().getLogger().info(mat.name() + " is not an item");
                }
            }

            // save using jackson https://stackabuse.com/reading-and-writing-json-in-java/

            File recipeFile = new File(PeakCraft.instance.getDataFolder() + File.separator + fileName);

            OutputStream outputStream = new FileOutputStream(recipeFile);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(outputStream, items);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
