package com.revature.util;

import com.revature.annotations.ColumnField;

import java.lang.reflect.Field;

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

    public String getDatatype() {
        return field.getType().getSimpleName();
    }

    //do all constraints, etc.
    public String getConstraints() {
        ColumnField col = field.getAnnotation(ColumnField.class);
        StringBuilder res = new StringBuilder();

        if (col.pkey()) {
            res.append("PRIMARY KEY ");
        }

        if (col.canBeNull() == false) {
            res.append("NOT NULL ");
        }

        if (col.unique()) {
            res.append("UNIQUE ");
        }

        return res.toString();
    }
}
