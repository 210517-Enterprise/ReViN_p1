package com.revature.repositories;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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

	public void deleteObject(Metamodel mm, Object o) {
		try (Connection conn = conFact.getConnection()) {
			String sql = "DELETE FROM " + mm.getTableName() + " WHERE " + mm.getPrimaryKey() + "= ?";

			PreparedStatement pstmt = conn.prepareStatement(sql.toString());			
			pstmt.setInt(1, getPrimaryKey(mm, o));
			System.out.println(pstmt);
			pstmt.execute();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public void updateObject(Metamodel mm, Object objToUpdate, Object valueForUpdate) {
		try (Connection conn = conFact.getConnection()) {

			List<Column> cols = mm.getColumns();

			for(Column col : cols) {
				if(col.getColName().equals(objToUpdate)){
					String sql = "UPDATE " + mm.getTableName() + " SET " + col.getColName() + "= ? WHERE "
							+ mm.getPrimaryKey() + "= ?";
					
					PreparedStatement pstmt = conn.prepareStatement(sql.toString());
					
					pstmt.setObject(1, valueForUpdate);
					pstmt.setInt(2, getPrimaryKey(mm, objToUpdate));
					
					pstmt.executeQuery();
				}
			}


		} catch (SQLException | SecurityException | IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public List<Object> readAllObject(Metamodel mm) {
		
		List<Object> objects = new ArrayList<Object>();
		
		try (Connection conn = conFact.getConnection()) {
			String sql = "SELECT * FROM " + mm.getTableName();
			System.out.println(sql);
			ResultSet rs = conn.prepareStatement(sql).executeQuery();
			System.out.println(rs);
			
			int i = 0;
			while(rs.next()) {
				
				Object object = null;
				try {
					object = Class.forName(mm.getClassName()).getDeclaredConstructor().newInstance();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				List<Column> cols = mm.getColumns();
				for (Column col : cols) {
					//object += col.getColName()+"="+rs.getString(col.getColName())+":";
					Field field = null;
					try {
						field = Class.forName(mm.getClassName()).getDeclaredField(col.getColName());
					} catch (NoSuchFieldException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					field.setAccessible(true);
					try {
						if (field.getType().getName().equals("double")){
							field.set(object, ((BigDecimal) rs.getObject(col.getColName())).doubleValue());
						}else {
							field.set(object, rs.getObject(col.getColName()));
						}
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				System.out.println(object);
				objects.add(object);
				//System.out.println(rs.getInt("id") + " " + rs.getString("username") + " " + rs.getString("pwd") + " " + rs.getObject("accounts") + " " + rs.getBoolean("citizen") + " " + rs.getInt("net_worth"));
				i++;
			}
			System.out.println("rs.next call count " + i);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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
				try {
					object = Class.forName(mm.getClassName()).getDeclaredConstructor().newInstance();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				List<Column> cols = mm.getColumns();
				for (Column col : cols) {
					//object += col.getColName()+"="+rs.getString(col.getColName())+":";
					Field field = null;
					try {
						field = Class.forName(mm.getClassName()).getDeclaredField(col.getColName());
					} catch (NoSuchFieldException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					field.setAccessible(true);
					try {
						if (field.getType().getName().equals("double")){
							field.set(object, ((BigDecimal) rs.getObject(col.getColName())).doubleValue());
						}else {
							field.set(object, rs.getObject(col.getColName()));
						}
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				System.out.println(object);
				i++;
			}
			System.out.println("rs.next call count " + i);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object;
	}
}