package com.revature.model;

import com.revature.annotations.ColumnField;
import com.revature.annotations.Table;
import com.revature.util.Column;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Metamodel<T> {
    private Class<T> clazz;
    private List<Column> columnFields = new ArrayList<>();
    private Map<String, String> colNameToFieldName;
    //add fields needed

    public Metamodel(Class<T> clazz) {
        this.clazz = clazz;
        colNameToFieldName = new HashMap<>();
        columnFields = setColumnFields();
    }

    private List<Column> setColumnFields() {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            columnFields.add(new Column(field));
            colNameToFieldName.put(field.getAnnotation(ColumnField.class).columnName(), field.getName());
        }

        return columnFields;
    }

    public void setPrimaryKey(Class<T> clazz, T obj, int id) {
        try {
            for (Column col : columnFields) {
                if (col.getConstraints().contains("PRIMARY KEY")) {
                    Field f = clazz.getDeclaredField(getJavaName(col.getColName()));
                    f.setAccessible(true);
                    f.set(obj, id);
                    break;
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public String getJavaName (String sqlColName) {
        return colNameToFieldName.get(sqlColName);
    }

    public String getPrimaryKey() {
        Field[] fields = clazz.getDeclaredFields();
        String pk = "";
        for (Field field : fields) {
            ColumnField col = field.getAnnotation(ColumnField.class);
            if (col != null && col.pkey()) {
                pk = col.columnName();
            }
        }
        return pk;
    }

    public String getTableName() {
        return clazz.getAnnotation(Table.class).tableName();
    }

    public String getClassName() {
        return clazz.getName();
    }

    public List<Column> getColumns() {
        return columnFields;
    }

    //etc.
}
