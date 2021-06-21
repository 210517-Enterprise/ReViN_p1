package com.revature;

import com.revature.model.Metamodel;
import com.revature.models.User;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MetamodelTests {

    @Test
    public void test_GetAllCols() {
        Metamodel mm = new Metamodel(User.class);
        assertEquals(4, mm.getColumns().size());
    }

    @Test
    public void test_GetPK() {
        Metamodel mm = new Metamodel(User.class);
        assertEquals("id", mm.getPrimaryKey());
    }

    @Test
    public void test_GetTableName() {
        Metamodel mm = new Metamodel(User.class);
        assertEquals("User", mm.getTableName());
    }
}
