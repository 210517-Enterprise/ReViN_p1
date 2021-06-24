package com.revature.util;

import com.revature.annotations.ColumnField;
import com.revature.annotations.Table;
import com.revature.model.Metamodel;

import java.lang.reflect.Field;
import java.util.List;

public class Column {
    private Field field;

    public Column(Field field) {
        if (field.getAnnotation(ColumnField.class) == null) {
            throw new IllegalStateException("Cannot create Column object! Provided field, " + field.getName() +
                    "is not annotated with @Column");
        }
        this.field = field;
    }

    public String getColName() {
        return field.getAnnotation(ColumnField.class).columnName();
    }

    public String getName() {
        return field.getName();
    }
    public String getDatatype() {
        ColumnField col = field.getAnnotation(ColumnField.class);
        if (col.isSerial()) {
            return "";
        }

        String javaType = field.getType().getSimpleName();
        String sqlType = "";
        switch (javaType) {
            case "Integer":
            case "int":
            case "Byte":
            case "byte":
            case "Short":
            case "short":
                sqlType = "INT";
                break;
            case "Long":
            case "long":
            case "Float":
            case "float":
            case "Double":
            case "double":
                sqlType = "NUMERIC";
                break;
            case "Boolean":
            case "boolean":
                sqlType = "BOOLEAN";
                break;
            case "Character":
            case "char":
                sqlType = "CHAR";
                break;
            default:
                sqlType = "TEXT";
                break;
        }

        return sqlType;
    }

    //do all constraints, etc.
    public String getConstraints() {
        ColumnField col = field.getAnnotation(ColumnField.class);
        StringBuilder res = new StringBuilder();

        if (col.isSerial()) {
            res.append("SERIAL ");
        }

        if (col.pkey()) {
            res.append("PRIMARY KEY ");
        }

        if (!col.canBeNull()) {
            res.append("NOT NULL ");
        }

        if (col.unique()) {
            res.append("UNIQUE ");
        }

        if (col.fkey()) {
            Metamodel tempMeta = new Metamodel(field.getAnnotation(ColumnField.class).fClass());
            String tableName = tempMeta.getTableName();
            String pkName = tempMeta.getPrimaryKey();
            res.append("REFERENCES " + tableName + "(" + pkName + ") ");
        }

        return res.toString();
    }
}
