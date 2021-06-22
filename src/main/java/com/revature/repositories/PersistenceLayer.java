package com.revature.repositories;

import com.revature.annotations.ColumnField;
import com.revature.connection.ConnectionFactory;
import com.revature.model.Metamodel;
import com.revature.util.Column;

import java.sql.Connection;
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
                        .append(datatype == "int" ? "int" : "varchar(50)")
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
}
