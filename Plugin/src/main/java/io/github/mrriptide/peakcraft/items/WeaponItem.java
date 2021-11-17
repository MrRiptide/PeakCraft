package io.github.mrriptide.peakcraft.items;

import io.github.mrriptide.peakcraft.util.MySQLHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;

public class WeaponItem extends EnchantableItem {

    public WeaponItem(){

    }

    public WeaponItem(Item item){
        super(item);
    }

    public static Item loadFromResultSet(ResultSet resultSet) throws SQLException {
        return loadFromResultSet(resultSet, new WeaponItem());
    }
    public static Item loadFromResultSet(ResultSet resultSet, Item item) throws SQLException {
        WeaponItem newItem = (WeaponItem) Item.loadFromResultSet(resultSet, item);

        Connection conn = MySQLHelper.getConnection();
        PreparedStatement statement = conn.prepareStatement("""
SELECT * FROM item_attributes WHERE item_id = ? AND attribute = 'damage';
""");
        statement.setString(1, newItem.id);

        ResultSet attributeResultSet = statement.executeQuery();

        if (attributeResultSet.next()){
            newItem.setAttribute("damage", attributeResultSet.getDouble("value"));
        } else {
            newItem.setAttribute("damage", 0.0);
        }

        return item;
    }

    public static boolean validateType(String type){
        return Arrays.asList("weapon", "sword").contains(type);
    }

    @Override
    public Item clone(){
        return new WeaponItem(super.clone());
    }

}
