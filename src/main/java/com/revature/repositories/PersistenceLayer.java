package com.revature.repositories;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import java.math.BigDecimal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.revature.annotations.ColumnField;
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
			String tablePK = mm.getTableName() + "." + mm.getPrimaryKey();

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
			numPrepared.append(")");

			sql.append(numPrepared);

			int count = 1;
			sql.append(" RETURNING " + tablePK);
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

			ResultSet rs = pstmt.executeQuery();
			rs.next();
			return rs.getInt(1);
		} catch (SQLException | NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public static int getPrimaryKey(Metamodel mm, Object o) {
		int id = 0;
		try {
			for (Method m : Class.forName(mm.getClassName()).getMethods()) {
				// This assumes that getId is the method for getting the PrimaryKey of an object
				// Maybe there is some way that we could create/set an annotation that would
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

//		We could also set the PrimaryKey to be public, but then User, etc. would not be a bean 			
//		try {
//			//This fails because the id field is private
//			id = Class.forName(mm.getClassName()).getDeclaredField(mm.getPrimaryKey()).getInt(o);
//		} catch (IllegalArgumentException e) {
//			e.printStackTrace();
//		} catch (IllegalAccessException e) {
//			e.printStackTrace();
//		} catch (NoSuchFieldException e) {
//			e.printStackTrace();
//		} catch (SecurityException e) {
//			e.printStackTrace();
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		}

		return id;
	}

	public void deleteObject(Metamodel mm, Object objToDelete) {
		try (Connection conn = conFact.getConnection()) {
			String sql = "DELETE FROM " + mm.getTableName() + " WHERE " + mm.getPrimaryKey() + "= ?";

			List<Column> cols = mm.getColumns();
			// for loop to create sql statement
			PreparedStatement pstmt = conn.prepareStatement(sql.toString());
			for (Column col : cols) {
				String sqlColName = col.getColName();
				String javaColName = mm.getJavaName(sqlColName);

				Object insert = null;

				try {
					Field fToInsert = objToDelete.getClass().getDeclaredField(javaColName);
					if (Modifier.isPrivate(fToInsert.getModifiers())) {
						fToInsert.setAccessible(true);
					}
					insert = fToInsert.get(objToDelete);
				} catch (NoSuchFieldException | IllegalAccessException e) {
					e.printStackTrace();
				}
				if (col.getConstraints().contains("PRIMARY KEY")) {
					pstmt.setObject(1, insert);
				}
			}
			


			System.out.println(pstmt);
			
			pstmt.execute();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public boolean updateObject(Metamodel mm, Object objToUpdate) {
		try (Connection conn = conFact.getConnection()) {
			StringBuilder sql = new StringBuilder("UPDATE " + mm.getTableName() + " SET ");
			StringBuilder qualifier = new StringBuilder(" WHERE ");

			List<Column> cols = mm.getColumns();
			// for loop to create sql statement
			for (Column col : cols) {
				String columnName = col.getColName();
				// Skip if serial
				if (col.getConstraints().contains("PRIMARY KEY")) {
					qualifier.append(columnName).append(" = ?");
				} else {
					if (col.getConstraints().equalsIgnoreCase("serial")) {
						continue;
					}

					sql.append(columnName).append(" = ?, ");

				}
				

			}

			int index = sql.lastIndexOf(", ");
			sql.delete(index, index + 2);
			sql.append(qualifier);

			PreparedStatement pstmt = conn.prepareStatement(sql.toString());

			int count = 1;
			// for loop to add updated values to sql statement
			for (Column col : cols) {
				String sqlColName = col.getColName();
				String javaColName = mm.getJavaName(sqlColName);

				Object insert = null;

				try {
					Field fToInsert = objToUpdate.getClass().getDeclaredField(javaColName);
					if (Modifier.isPrivate(fToInsert.getModifiers())) {
						fToInsert.setAccessible(true);
					}
					insert = fToInsert.get(objToUpdate);
				} catch (NoSuchFieldException | IllegalAccessException e) {
					e.printStackTrace();
				}
				try {
					if (col.getConstraints().contains("PRIMARY KEY")) {
						pstmt.setObject(cols.size(), insert);
					} else {

						pstmt.setObject(count, insert);

						count++;
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			System.out.println(pstmt);
			pstmt.execute();
			return true;

		} catch (SQLException | SecurityException | IllegalArgumentException | NullPointerException e) {

			e.printStackTrace();
		}
		return false;
	}


	public List<Object> readAllObject(Metamodel mm) {
		List<Object> objects = new ArrayList<>();


		try (Connection conn = conFact.getConnection()) {
			String sql = "SELECT * FROM " + mm.getTableName();
			System.out.println(sql);
			ResultSet rs = conn.prepareStatement(sql).executeQuery();
			System.out.println(rs);

			int i = 0;

			while(rs.next()) {
				Object object = Class.forName(mm.getClassName()).getDeclaredConstructor().newInstance();

				List<Column> cols = mm.getColumns();
				for (Column col : cols) {
					String sqlColName = col.getColName();
					String javaColName = mm.getJavaName(sqlColName);

					Field field = Class.forName(mm.getClassName()).getDeclaredField(javaColName);
					field.setAccessible(true);

					if (field.getType().getName().equals("double")){
						field.set(object, ((BigDecimal) rs.getObject(sqlColName)).doubleValue());
					}else if (field.getType().getName().equals("float")){
						field.set(object, ((BigDecimal) rs.getObject(sqlColName)).floatValue());
					}else if (field.getType().getName().equals("long")){
						field.set(object, ((BigDecimal) rs.getObject(sqlColName)).longValue());
					}else {
						field.set(object, rs.getObject(sqlColName));
					}
				}
				System.out.println(object);
				objects.add(object);

				i++;
			}
			System.out.println("rs.next call count " + i);
		} catch (SQLException | ClassNotFoundException | NoSuchMethodException | NoSuchFieldException
				| InvocationTargetException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return objects;
	}


	public Object readObject(Metamodel mm, int primaryKey) {
		Object object = null;


		try (Connection conn = conFact.getConnection()) {
			String sql = "SELECT * FROM " + mm.getTableName() + " WHERE " + mm.getPrimaryKey() + " = " + primaryKey;
			System.out.println(sql);
			ResultSet rs = conn.prepareStatement(sql).executeQuery();
			System.out.println(rs);

			int i = 0;

			while(rs.next()) {
				object = Class.forName(mm.getClassName()).getDeclaredConstructor().newInstance();

				List<Column> cols = mm.getColumns();
				for (Column col : cols) {
					String sqlColName = col.getColName();
					String javaColName = mm.getJavaName(sqlColName);

					Field field = Class.forName(mm.getClassName()).getDeclaredField(javaColName);
					field.setAccessible(true);

					if (field.getType().getName().equals("double")) {
						field.set(object, ((BigDecimal) rs.getObject(sqlColName)).doubleValue());
					} else if (field.getType().getName().equals("float")) {
						field.set(object, ((BigDecimal) rs.getObject(sqlColName)).floatValue());
					} else if (field.getType().getName().equals("long")) {
						field.set(object, ((BigDecimal) rs.getObject(sqlColName)).longValue());
					} else {
						field.set(object, rs.getObject(sqlColName));
					}

				}

				System.out.println(object);
				i++;
			}
			System.out.println("rs.next call count " + i);
		} catch (SQLException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException
				| NoSuchFieldException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}

		return object;
	}
}