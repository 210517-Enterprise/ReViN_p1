package com.revature.models;

import com.revature.annotations.ColumnField;
import com.revature.annotations.Table;

import java.util.List;

@Table(tableName = "User")
public class User {
    @ColumnField(columnName = "id", pkey = true)
    private int id;

    @ColumnField(columnName = "username", unique = true, canBeNull = false)
    private String username;

    @ColumnField(columnName = "pwd", canBeNull = false)
    private String password;

    @ColumnField(columnName = "account", fkey = true)
    private List<Account> accounts;
}
