package com.digsigmobile.persistence.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.digsigmobile.datatypes.UserId;
import com.digsigmobile.exceptions.DatabaseException;
import com.digsigmobile.exceptions.InvalidInputException;
import com.digsigmobile.persistence.Command;

public class CmdRetrieveMaxUserId extends Command 
{

	/**
	 * UserId object to return the result
	 */
	private UserId userId = null;
	
	/**
	 * Initializes SQL command
	 * @param email
	 */
	public CmdRetrieveMaxUserId() {
		this.sqlCommand = "SELECT MAX(UserID) FROM `tbl_UserDetails`";
		this.isQuery = true;
	}
	
	/**
	 * method that retrieves the maximum user id from the db if rows are present
	 * If the query returns empty set, method returns 1 as new user id 
	 */
	@Override
	public void run() throws DatabaseException {
		try 
		{
			if (resultSet.next()) 
			{
				int max = resultSet.getInt(1);
				userId = new UserId(++max);
			}
			else
				userId = new UserId(1);
		} 
		catch (SQLException se) {
			
			throw new DatabaseException(se.getMessage());
		}
		
		catch (InvalidInputException e) {
			
			throw new DatabaseException(e.getMessage());
		}
		finally{
		     if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e1) {
				    throw new DatabaseException(e1.getMessage());
				}
			}
			if (resultSet != null){
				try {
					resultSet.close();
				} catch (SQLException e1) {
				    throw new DatabaseException(e1.getMessage());
				}
			}
			}
		
	}

	
	/**
	 * Overrides method to prepare a statement specific for
	 * retrieving an user.
	 */
	@Override
	protected PreparedStatement prepareCommand(Connection conn) throws DatabaseException {
		try {
			preparedStatement = conn.prepareStatement(this.sqlCommand);
			return preparedStatement; 
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}
	}

	/**
	 * Method to get the UserId retrieved from the database
	 * @return userId
	 */
	public UserId getUserId() {
		return userId;
	}

}