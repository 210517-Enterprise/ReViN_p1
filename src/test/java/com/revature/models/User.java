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

    @ColumnField(columnName = "accounts", fkey = true)
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}

	public boolean isCitizen() {
		return isCitizen;
	}

	public void setCitizen(boolean isCitizen) {
		this.isCitizen = isCitizen;
	}

	public double getNetWorth() {
		return netWorth;
	}

	public void setNetWorth(double netWorth) {
		this.netWorth = netWorth;
	}
    
    
}
