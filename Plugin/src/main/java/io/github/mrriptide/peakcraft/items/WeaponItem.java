package io.github.mrriptide.peakcraft.items;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class WeaponItem extends EnchantableItem {

    public WeaponItem(){

    }

    public WeaponItem(Item item){
        super(item);
    }

    public static Item loadFromResultSet(Connection conn, ResultSet resultSet) throws SQLException {
        return loadFromResultSet(conn, resultSet, new WeaponItem());
    }

    public static Item loadFromResultSet(Connection conn, ResultSet resultSet, Item item) throws SQLException {
        WeaponItem newItem = (WeaponItem) EnchantableItem.loadFromResultSet(conn, resultSet, item);

        PreparedStatement statement = conn.prepareStatement("""
SELECT * FROM item_attributes WHERE item_id = ? AND attribute_id = 'damage';
""");
        statement.setString(1, newItem.id);

        ResultSet attributeResultSet = statement.executeQuery();

        if (attributeResultSet.next()){
            newItem.setAttribute("damage", attributeResultSet.getDouble("value"));
        } else {
            newItem.setAttribute("damage", 0.0);
        }
        attributeResultSet.close();
        statement.close();

        return item;
    }

    public static boolean validateType(String type){
        return Arrays.asList("weapon", "sword").contains(type.toLowerCase());
    }

    @Override
    public Item clone(){
        return new WeaponItem(super.clone());
    }

}
