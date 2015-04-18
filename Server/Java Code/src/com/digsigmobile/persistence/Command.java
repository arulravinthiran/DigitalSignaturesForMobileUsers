package com.digsigmobile.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.activation.MimeTypeParseException;

import com.digsigmobile.exceptions.DatabaseException;
import com.digsigmobile.exceptions.InvalidInputException;

/**
 * This is the base class of the Command design pattern,
 * which provides a hierarchy of different Commands to be
 * executed by the Persistence tier. This is an abstract
 * class and each subclass must implement its run method. 
 * @author -Bernardo
 *
 */
public abstract class Command {

	/**
	 * The SQL String that represents the command
	 * to be executed in the database.
	 */
	protected String sqlCommand = null;
	
	/**
	 * Attribute that determines is a Command is
	 * a query or a Insert/Update/Delete. Initialized
	 * with false.
	 */
	protected boolean isQuery = false;
	
	/**
	 * The prepared SQL command.
	 */
	protected PreparedStatement preparedStatement = null;
	
	/**
	 * The result set provided by the database after
	 * the command's execution.
	 */
	protected ResultSet resultSet = null;

	/**
	 * Abstract method to be implemented by each different
	 * Command class. This method will be called after the
	 * Persistence object runs. Therefore, in this method
	 * the Command class can do any computation with the
	 * ResultSet.
	 * @throws DatabaseException
	 * @throws InvalidInputException 
	 */
	public abstract void run() throws DatabaseException, InvalidInputException,MimeTypeParseException;
	
	/**
	 * Abstract method that prepares the command by inserting
	 * the needed values.
	 * @return
	 */
	protected abstract PreparedStatement prepareCommand(Connection conn) 
			throws DatabaseException;

	public void setResultSet(ResultSet resultSet) {
		this.resultSet = resultSet;
	}

	public PreparedStatement getCommand(Connection conn) throws DatabaseException{
		return prepareCommand(conn);
	}

	public void setCommand(String sqlCommand) {
		this.sqlCommand = sqlCommand;
	}

	public boolean isQuery() {
		return isQuery;
	}
	
}