package io.github.mrriptide.peakcraft.items;

import io.github.mrriptide.peakcraft.items.fullsetbonus.FullSetBonus;
import io.github.mrriptide.peakcraft.items.fullsetbonus.FullSetBonusManager;
import io.github.mrriptide.peakcraft.util.MySQLHelper;

import javax.xml.transform.Result;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class ArmorItem extends EnchantableItem {
    private String set;

    public ArmorItem() {}

    public ArmorItem(Item item){
        super(item);
    }

    public static Item loadFromResultSet(Connection conn, ResultSet resultSet) throws SQLException {
        return loadFromResultSet(conn, resultSet, new ArmorItem());
    }
    public static Item loadFromResultSet(Connection conn, ResultSet resultSet, Item item) throws SQLException {
        ArmorItem newItem = (ArmorItem) Item.loadFromResultSet(conn, resultSet, item);

        PreparedStatement statement = conn.prepareStatement("""
SELECT * FROM item_attributes WHERE item_id = ?;
""");
        statement.setString(1, newItem.id);

        List<String> accepted_attributes = Arrays.asList("health", "defense");

        ResultSet attributeResultSet = statement.executeQuery();

        while (attributeResultSet.next()){
            if (accepted_attributes.contains(attributeResultSet.getString("attribute_id"))){
                newItem.setAttribute(attributeResultSet.getString("attribute_id"), attributeResultSet.getDouble("value"));
            }
        }

        attributeResultSet.close();
        statement.close();

        statement = conn.prepareStatement("""
SELECT * FROM armor_sets WHERE item_id = ?
""");
        statement.setString(1, newItem.id);

        ResultSet setResultSet = statement.executeQuery();
        if (setResultSet.next()){
            newItem.set = setResultSet.getString("set");
        } else {
            newItem.set = "";
        }

        return item;
    }

    public static boolean validateType(String type){
        return Arrays.asList("armor", "chestplate", "helmet", "leggings", "boots").contains(type.toLowerCase());
    }

    @Override
    public Item clone(){
        ArmorItem item = new ArmorItem(super.clone());
        item.set = set;
        return item;
    }

    public String getSetName(){
        return this.set;
    }

    public FullSetBonus getSet(){
        if (FullSetBonusManager.validSet(this.set))
            return FullSetBonusManager.getSet(this.set);
        else
            return null;
    }

}
