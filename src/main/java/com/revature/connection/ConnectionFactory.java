package com.revature.connection;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.dbcp2.BasicDataSource;

import com.revature.util.Database;

public class ConnectionFactory {
	private BasicDataSource ds;

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ConnectionFactory(Database db) {
    	try {
    		Properties props = new Properties();
    		props.load(new FileReader("src/main/resources/application.properties"));
    		ds = new BasicDataSource();
    		ds.setUrl(props.getProperty("url"));
    		ds.setUsername(props.getProperty("username"));
    		ds.setPassword(props.getProperty("password"));
            ds.setMinIdle(db.getMinIdle());
            ds.setMaxIdle(db.getMaxIdle());
            ds.setMaxOpenPreparedStatements(db.getMaxOpenPreparedStatements());

    	} catch(IOException e) {
    		e.printStackTrace();
    	}

    }

	public Connection getConnection() throws SQLException {
		return ds.getConnection();
	}

}
