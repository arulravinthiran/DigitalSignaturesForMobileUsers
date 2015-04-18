package com.digsigmobile.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Class opens connection to mySQL and returns the connection string and driver object
 * @author Arul
 * created on May 29 2014
 */
public class ConnectionDB 
{
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	private static final String DB_URL = "jdbc:mysql://localhost/digsigmobile";
	
	private static final String USER = "root";
	private static final String PASSWORD = "Arul_123";
	
	/**
	 * method returns connection object
	 * @return
	 * @throws SQLException 
	 */
	public static Connection getConnectionObject() throws SQLException 
	{
		Connection connObj = null;
		try 
		{
			connObj = DriverManager.getConnection(DB_URL,USER,PASSWORD);
			return connObj;
		} 
		catch (SQLException e) 
		{
			throw e;
		}
	}
	
	/**
	 * method returns driver object as a string
	 * @return
	 */
	
	public static String getDriverObject() 
	{
		return JDBC_DRIVER;
	}
	
}