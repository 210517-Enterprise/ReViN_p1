package com.revature;

import com.revature.annotations.ColumnField;
import com.revature.models.User;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;

public class AnnotationTests {

    @Test
    public void test_GetAllColumns() {
        Class<?> clazz = User.class;
        Field[] fields = clazz.getDeclaredFields();
        assertEquals(4, fields.length);
    }

    @Test
    public void test_GetPrimaryKey() {
        Class<?> clazz = User.class;
        Field[] fields = clazz.getDeclaredFields();
        String columnName = "";
        for (Field f : fields) {
            ColumnField column = f.getAnnotation(ColumnField.class);
            if (column.pkey()) {
                columnName = column.columnName();
            }
        }

        assertEquals("id", columnName);
    }
}
