package com.revature.models;

import com.revature.annotations.ColumnField;
import com.revature.annotations.Table;

@Table(tableName = "Account")
public class Account {

    @ColumnField(columnName = "id", pkey = true, isSerial = true)
    private int id;

    @ColumnField(columnName = "acc_owner", fkey = true, fClass = User.class)
    private int accOwner;

    @ColumnField(columnName = "balance")
    private double balance;

    public Account() {
        super();
    }

    public Account(int accOwner, double balance) {
        this.accOwner = accOwner;
        this.balance = balance;
    }

    public Account(int id, int accOwner, double balance) {
        this.id = id;
        this.accOwner = accOwner;
        this.balance = balance;
    }
}
