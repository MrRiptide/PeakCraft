package io.github.mrriptide.peakcraft.items;

import io.github.mrriptide.peakcraft.items.fullsetbonus.FullSetBonus;
import io.github.mrriptide.peakcraft.items.fullsetbonus.FullSetBonusManager;
import io.github.mrriptide.peakcraft.util.MySQLHelper;

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

    public static Item loadFromResultSet(ResultSet resultSet) throws SQLException {
        return loadFromResultSet(resultSet, new ArmorItem());
    }
    public static Item loadFromResultSet(ResultSet resultSet, Item item) throws SQLException {
        ArmorItem newItem = (ArmorItem) Item.loadFromResultSet(resultSet, item);

        Connection conn = MySQLHelper.getConnection();
        PreparedStatement statement = conn.prepareStatement("""
SELECT * FROM item_attributes WHERE item_id = ?;
""");
        statement.setString(1, newItem.id);

        List<String> accepted_attributes = Arrays.asList("health", "defense");

        ResultSet attributeResultSet = statement.executeQuery();

        while (attributeResultSet.next()){
            if (accepted_attributes.contains(attributeResultSet.getString("attribute"))){
                newItem.setAttribute(attributeResultSet.getString("attribute"), attributeResultSet.getDouble("value"));
            }
        }

        return item;
    }

    public static boolean validateType(String type){
        return Arrays.asList("armor", "chestplate", "helmet", "leggings", "boots").contains(type);
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
