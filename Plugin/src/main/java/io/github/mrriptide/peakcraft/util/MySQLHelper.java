package io.github.mrriptide.peakcraft.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public abstract class MySQLHelper {
    public static String getPlaceholders(int count){
        StringBuilder builder = new StringBuilder("(");
        for (int i = 0; i < count; i++){
            if (i != 0){
                builder.append(",");
            }
            builder.append("?");
        }
        return builder.append(")").toString();
    }


    public static void bulkInsert(Connection connection, String queryStart, ArrayList<Object[]> values) throws SQLException {
        String placeholders = getPlaceholders(values.get(0).length);

        StringBuilder builder = new StringBuilder(queryStart);

        for (int i = 0; i < values.size(); i++){
            if (i != 0){
                builder.append(",");
            }
            builder.append(placeholders);
        }

        PreparedStatement statement = connection.prepareStatement(builder.toString());

        int parameterIndex = 1;
        for (Object[] valueSet : values){
            for (Object value : valueSet){
                statement.setObject(parameterIndex++, value);
            }
        }

        statement.execute();
        statement.close();
    }
}
