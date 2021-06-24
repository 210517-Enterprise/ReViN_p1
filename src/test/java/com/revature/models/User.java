package com.revature.models;

import com.revature.annotations.ColumnField;
import com.revature.annotations.Table;

import java.util.List;

@Table(tableName = "users")
public class User {
    @ColumnField(columnName = "id", pkey = true, isSerial = true)
    private int id;

    @ColumnField(columnName = "username", unique = true, canBeNull = false)
    private String username;

    @ColumnField(columnName = "pwd", canBeNull = false)
    private String password;

    @ColumnField(columnName = "accounts")
    private List<Account> accounts;

    @ColumnField(columnName = "citizen")
    private boolean isCitizen = true;

    @ColumnField(columnName = "net_worth")
    private double netWorth = 1_000_000;

    public User() {
        super();
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    public User(int id, String username, String password, List<Account> accounts) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.accounts = accounts;
    }
}
