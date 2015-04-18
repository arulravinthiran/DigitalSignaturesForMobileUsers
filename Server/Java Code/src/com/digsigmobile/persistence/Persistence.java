package com.digsigmobile.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.activation.MimeTypeParseException;

import com.digsigmobile.exceptions.DatabaseException;
import com.digsigmobile.exceptions.InvalidInputException;

/**
 * Controls all the database commands.
 * Follows the Singleton pattern: can't be instantiated more than once.
 * @author -Bernardo
 */
public class Persistence {

	/*
	 * A static variable initialized with null holds the only
	 * instance possible for this class.
	 */
	private static Persistence persistenceInstance = null;
	
	/**
	 * Connection object
	 */
	private Connection connObj = null;
	
	/**
	 * Connection status
	 */
	private boolean connected = false;
	
	/*
	 * This private constructor makes sure that no one can 
	 * create a new Persistence object.
	 */
	private Persistence() {}
	
	/*
	 * To be able to use the Persistence class, you must call
	 * this method getInstance. It will create a new instance
	 * if there is none, or return the available instance.
	 */
	public static Persistence getInstance() {
		if (persistenceInstance == null) {
			persistenceInstance = new Persistence();
		}
		return persistenceInstance;
	}
	
	/**
	 * Returns true if the database is connected
	 */
	public boolean isConnected() {
		return this.connected;
	}
	
	/**
	 * Creates a new connection with the database
	 * @throws DatabaseException
	 */
	public void connect() throws DatabaseException {
			try {
				Class.forName(ConnectionDB.getDriverObject());
				connObj =  ConnectionDB.getConnectionObject();
				if(connObj != null)
				connected = true;
			} catch (Exception e) {
				throw new DatabaseException(e.getMessage());
			}
	}
	
	/**
	 * Disconnects with the database
	 * @throws DatabaseException
	 */
	public void disconnect() throws DatabaseException {
		try {
			if(connObj != null){
				connObj.close();
				connected = false;
				}
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}
	}
	
	/**
	 * Runs the SQL command
	 * @param Command cmd
	 */
	public void run(Command cmd) throws DatabaseException 
	{
		if (!isConnected()) connect();
		try {
			PreparedStatement preparedStatement = cmd.getCommand(connObj);
			int affectedRows = 0;
			if (cmd.isQuery()) {
				cmd.setResultSet(preparedStatement.executeQuery());
			} else{
				affectedRows = preparedStatement.executeUpdate();
				if (affectedRows != 1)
					throw new DatabaseException("Row not updated/inserted in the databasee");
			}
			cmd.run(); 
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException(e.getMessage());
		} catch (InvalidInputException e) {
			// TODO Auto-generated catch block
			throw new DatabaseException(e.getMessage());
		} catch (MimeTypeParseException e) {
			// TODO Auto-generated catch block
			throw new DatabaseException(e.getMessage());
		}
	}
}