package com.revature.util;

public class Database {
	private String sqlDatabase;
	private int minIdle;
	private int maxIdle;
	private int maxOpenPreparedStatements;

	/**
	 * Default constructor for the database, sets default values.
	 */
	public Database() {
		this.sqlDatabase = null;
	}

	public static boolean validate(Database database) {
		return ((database.getSqlDatabase() == null || database.getSqlDatabase().trim().equals(""))
				|| (database.getMinIdle() == -1) || (database.getMaxIdle() == -1)
				|| (database.getMaxOpenPreparedStatements() == -1));
	}

	public String getSqlDatabase() {
		return sqlDatabase;
	}

	public void setSqlDatabase(String sqlDatabase) {
		this.sqlDatabase = sqlDatabase;
	}

	public int getMinIdle() {
		return minIdle;
	}

	public void setMinIdle(int minIdle) {
		this.minIdle = minIdle;
	}

	public int getMaxIdle() {
		return maxIdle;
	}

	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}

	public int getMaxOpenPreparedStatements() {
		return maxOpenPreparedStatements;
	}

	public void setMaxOpenPreparedStatements(int maxOpenPreparedStatements) {
		this.maxOpenPreparedStatements = maxOpenPreparedStatements;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + maxIdle;
		result = prime * result + maxOpenPreparedStatements;
		result = prime * result + minIdle;
		result = prime * result + ((sqlDatabase == null) ? 0 : sqlDatabase.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Database other = (Database) obj;
		if (maxIdle != other.maxIdle)
			return false;
		if (maxOpenPreparedStatements != other.maxOpenPreparedStatements)
			return false;
		if (minIdle != other.minIdle)
			return false;
		if (sqlDatabase == null) {
			if (other.sqlDatabase != null)
				return false;
		} else if (!sqlDatabase.equals(other.sqlDatabase))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Database [sqlDatabase=" + sqlDatabase + ", minIdle=" + minIdle + ", maxIdle=" + maxIdle
				+ ", maxOpenPreparedStatements=" + maxOpenPreparedStatements + "]";
	}
}
