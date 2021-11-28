package io.github.mrriptide.peakcraft.items;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.mrriptide.peakcraft.PeakCraft;
import io.github.mrriptide.peakcraft.exceptions.ItemException;
import io.github.mrriptide.peakcraft.util.Formatter;
import io.github.mrriptide.peakcraft.util.MySQLHelper;
import io.github.mrriptide.peakcraft.util.PersistentDataManager;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ItemManager {

    private static String itemFilePath = "items.json";
    private static HashMap<String, Item> items = new HashMap<>();

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
            PeakCraft.getPlugin().getLogger().info("Loading " + resultSet.getString("id").toUpperCase() + " as a weapon");
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
}
