package com.revature.model;

import com.revature.annotations.ColumnField;
import com.revature.annotations.Table;
import com.revature.util.Column;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Metamodel<T> {
    private Class<T> clazz;
    private List<Column> columnFields = new ArrayList<>();
    //add fields needed

    public Metamodel(Class<T> clazz) {
        this.clazz = clazz;
        columnFields = setColumnFields();
    }

    private List<Column> setColumnFields() {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            columnFields.add(new Column(field));
        }

        return columnFields;
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
