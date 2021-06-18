package com.revature.models;

import com.revature.annotations.ColumnField;
import com.revature.annotations.Table;

@Table(tableName = "Account")
public class Account {

    @ColumnField(columnName = "id", pkey = true)
    private int id;

    @ColumnField(columnName = "acc_owner", fkey = true)
    private int accOwner;

    @ColumnField(columnName = "balance")
    private double balance;
}
