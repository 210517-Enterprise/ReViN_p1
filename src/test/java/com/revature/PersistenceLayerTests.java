package com.revature;

import com.revature.model.Metamodel;
import com.revature.models.User;
import com.revature.util.Column;
import org.junit.Test;

import java.util.List;

public class PersistenceLayerTests {
    @Test
    public void test_CreateTable() {

        Metamodel mm = new Metamodel(User.class);
        StringBuilder sql = new StringBuilder("CREATE TABLE " + mm.getTableName() + " (\n");

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

        sql.deleteCharAt(sql.length()-1);
        sql.append(")");

        System.out.println(sql.toString());
    }
}
