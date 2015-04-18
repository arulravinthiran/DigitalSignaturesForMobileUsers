package com.digsigmobile.persistence.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.digsigmobile.datatypes.UserId;
import com.digsigmobile.exceptions.DatabaseException;
import com.digsigmobile.persistence.Command;

public class CmdUpdateUserCertStatus extends Command 
{

	/**
	 * Receives a boolean to set the status
	 */
	private boolean status;
	
	/**
	 * Receives a UserId object to identify the user
	 */
	private UserId userid;
	
	/**
	 * Initializes SQL command and sets up status
	 * @param user
	 */
	public CmdUpdateUserCertStatus(UserId id, boolean status) {
		this.status = status;
		this.userid = id;
		this.sqlCommand = "UPDATE `tbl_userdetails` SET `HasCertificate` = ? WHERE `UserID` = ?";
		this.isQuery = false;
	}
	
	/**
	 * Since this is an update command, there's no need
	 * to run any computations with the result set, so
	 * this method is a no-operation except closing of opened resources.
	 */
	@Override
	public void run() throws DatabaseException {
		if (preparedStatement != null)
			try {
				preparedStatement.close();
			} 
			catch (SQLException e1) {
			    throw new DatabaseException(e1.getMessage());
			}
	}

	
	/**
	 * Overrides method to prepare a statement specific for
	 * updating a user's certificate status.
	 */
	@Override
	protected PreparedStatement prepareCommand(Connection conn) throws DatabaseException {
		try {
			preparedStatement = conn.prepareStatement(this.sqlCommand);
			if(this.status)
			preparedStatement.setInt(1, 1);
			else
			preparedStatement.setInt(1, 0);
			preparedStatement.setInt(2, userid.getId());
			return preparedStatement;
		} 
		catch (SQLException e) {
			
			throw new DatabaseException(e.getMessage());
		}
		
	}

}
