package com.revature.model;

import com.revature.annotations.ColumnField;

import java.util.List;

public class Metamodel<T> {
    private Class<T> clazz;
    //add fields needed

    public Metamodel(Class<T> clazz) {
        this.clazz = clazz;
    }

    public ColumnField getPrimaryKey() {
        return null;
    }

    public List<ColumnField> getColumns() {
        return null;
    }

    //etc.
}
