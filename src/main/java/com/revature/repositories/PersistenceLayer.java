package com.revature.repositories;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.revature.connection.ConnectionFactory;
import com.revature.model.Metamodel;
import com.revature.util.Column;

public class PersistenceLayer {
	private ConnectionFactory conFact;

	public PersistenceLayer(ConnectionFactory conFact) {
		this.conFact = conFact;
	}

	public int createTable(Metamodel mm) {
		try (Connection conn = conFact.getConnection()) {
			StringBuilder sql = new StringBuilder("CREATE TABLE " + mm.getTableName() + " (");

			List<Column> cols = mm.getColumns();
			for (Column col : cols) {
				String name = col.getColName();
				String datatype = col.getDatatype();
				String constraints = col.getConstraints();
				sql.append(name).append(" ").append(datatype).append(" ").append(constraints).append(",");
			}

			// Delete last comma
			sql.deleteCharAt(sql.length() - 1);
			sql.append(")");

			System.out.println(sql.toString());
			conn.prepareStatement(sql.toString()).execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 1;
	}

	/*
	 * Adds a new Object with the corresponding Metamodel
	 * 
	 * @param mm The Metamodel associated with the new object
	 * 
	 * @param newObj The new object to added to the table
	 * 
	 * @return the id of the new object
	 */
	public int addObject(Metamodel mm, Object newObj) {
		try (Connection conn = conFact.getConnection()) {
			StringBuilder sql = new StringBuilder("INSERT INTO " + mm.getTableName() + " (");

			StringBuilder numPrepared = new StringBuilder("(");
			List<Column> cols = mm.getColumns();

			// Generate the ? for the prepared statement
			for (Column col : cols) {
				// Skip if serial
				if (col.getConstraints().contains("SERIAL")) {
					continue;
				}

				sql.append(col.getColName() + ",");
				numPrepared.append("?,");
			}

			// Delete last comma
			sql.deleteCharAt(sql.length() - 1);
			sql.append(") VALUES ");

			numPrepared.deleteCharAt(numPrepared.length() - 1);
			numPrepared.append(") RETURNING " + mm.getTableName() + ".id");

			sql.append(numPrepared);

			int count = 1;
			PreparedStatement pstmt = conn.prepareStatement(sql.toString());

			// Iterate through every field
			for (Column col : cols) {
				// Convert SQL col name to Java col name
				String sqlColName = col.getColName();
				String javaColName = mm.getJavaName(sqlColName);

				// Grab the specific field
				Field f = newObj.getClass().getDeclaredField(javaColName);

				// Access private fields
				f.setAccessible(true);

				// Skip if it's serial
				if (col.getConstraints().contains("SERIAL")) {
					continue;
				}

				// Get the value associated with that field in the object to be added
				pstmt.setObject(count, f.get(newObj));
				count++;
			}
			
			System.out.println(pstmt);
			ResultSet rs;

			// Generates a value if successful
			if ((rs = pstmt.executeQuery()) != null) {
				rs.next();
				int id = rs.getInt(1);
				System.out.println("The id returned from insert is "+id);
				return id;
			}
		} catch (SQLException | NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public void deleteObject(Metamodel mm, Object o) {
		try (Connection conn = conFact.getConnection()) {
			String sql = "DELETE FROM " + mm.getTableName() + " WHERE " + mm.getPrimaryKey() + "= ?";

			PreparedStatement pstmt = conn.prepareStatement(sql.toString());
			int id = 0;
			try {
				for (Method m : Class.forName(mm.getClassName()).getMethods()) {
					//This assumes that getId is the method for getting the PrimaryKey of an object
					//Maybe there is some way that we could create/set an annotation that would
					// specify the method which returns the PrimaryKey
					if (m.getName().equals("getId")) {
						id = (int) m.invoke(o);
						break;
					}
				}
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
//			We could also set the PrimaryKey to be public, but then User, etc. would not be a bean 			
//			try {
//				//This fails because the id field is private
//				id = Class.forName(mm.getClassName()).getDeclaredField(mm.getPrimaryKey()).getInt(o);
//			} catch (IllegalArgumentException e) {
//				e.printStackTrace();
//			} catch (IllegalAccessException e) {
//				e.printStackTrace();
//			} catch (NoSuchFieldException e) {
//				e.printStackTrace();
//			} catch (SecurityException e) {
//				e.printStackTrace();
//			} catch (ClassNotFoundException e) {
//				e.printStackTrace();
//			}
			
			pstmt.setInt(1, id);
			System.out.println(pstmt);
			pstmt.execute();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void updateObject(Metamodel mm, Object objToUpdate, Object valueForUpdate, Object primaryKey) {
		try (Connection conn = conFact.getConnection()) {

			List<Column> cols = mm.getColumns();

			for(Column col : cols) {
				if(col.getColName().equals(objToUpdate)){
					String sql = "UPDATE " + mm.getTableName() + " SET " + col.getColName() + "= ? WHERE "
							+ mm.getPrimaryKey() + "= ?";
					
					PreparedStatement pstmt = conn.prepareStatement(sql.toString());
					
					pstmt.setObject(1, valueForUpdate);
					pstmt.setObject(2, primaryKey);
					
					pstmt.executeQuery();
				}
			}


		} catch (SQLException | SecurityException | IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
