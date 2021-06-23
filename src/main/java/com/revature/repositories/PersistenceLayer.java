package com.revature.repositories;

import com.revature.annotations.ColumnField;
import com.revature.connection.ConnectionFactory;
import com.revature.model.Metamodel;
import com.revature.util.Column;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class PersistenceLayer {
    private ConnectionFactory conFact;

    public PersistenceLayer(ConnectionFactory conFact) {
        this.conFact = conFact;
    }

    public int createTable(Metamodel mm) {
        try(Connection conn = conFact.getConnection()) {
            StringBuilder sql = new StringBuilder("CREATE TABLE " + mm.getTableName() + " (");

            List<Column> cols = mm.getColumns();
            for (Column col : cols) {
                String name = col.getColName();
                String datatype = col.getDatatype();
                String constraints = col.getConstraints();
                sql.append(name)
                        .append(" ")
                        .append(datatype)
                        .append(" ")
                        .append(constraints)
                        .append(",");
            }

            //Delete last comma
            sql.deleteCharAt(sql.length()-1);
            sql.append(")");

            System.out.println(sql.toString());
            conn.prepareStatement(sql.toString()).execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public void addObject(Metamodel mm, Object newObj) {
        try(Connection conn = conFact.getConnection()) {
            StringBuilder sql = new StringBuilder("INSERT INTO " + mm.getTableName() + " (");

            StringBuilder numPrepared = new StringBuilder("(");
            List<Column> cols = mm.getColumns();
            for (Column col : cols) {
                sql.append(col.getColName()+",");
                numPrepared.append("?,");
            }

            //Delete last comma
            sql.deleteCharAt(sql.length()-1);
            sql.append(") VALUES ");

            numPrepared.deleteCharAt(numPrepared.length()-1);
            numPrepared.append(")");

            sql.append(numPrepared);

            int count = 0;
            PreparedStatement pstmt = conn.prepareStatement(sql.toString());
            for (Column col : cols) {
                count++;
                //Convert SQL col name to Java col name
                String sqlColName = col.getColName();
                String javaColName = mm.getJavaName(sqlColName);

                //Grab the specific field
                Field f = newObj.getClass().getDeclaredField(javaColName);

                //Access private fields
                f.setAccessible(true);


                //if (col.getConstraints().contains("SERIAL")) {
                //    continue;
                //}

                //Get the value associated with that field in the object to be added
                pstmt.setObject(count, f.get(newObj));
            }

            pstmt.execute();
        } catch (SQLException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
