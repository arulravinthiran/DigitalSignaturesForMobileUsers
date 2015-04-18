package com.digsigmobile.persistence.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.digsigmobile.beans.UserBean;
import com.digsigmobile.exceptions.DatabaseException;
import com.digsigmobile.persistence.Command;

public class CmdCreateUser extends Command {

	/**
	 * Receives an UserBean object complete with id
	 * included.
	 */
	private UserBean user;
	
	/**
	 * Initializes SQL command and sets up UserBean
	 * @param user
	 */
	public CmdCreateUser(UserBean user) {
		this.user = user;
		this.sqlCommand = "INSERT INTO TBL_USERDETAILS VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
		this.isQuery = false;
	}
	
	/**
	 * Since this is an insert command, there's no need
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
	 * creating a new user.
	 * Secondary Phone number can be null
	 * Secondary Email can be null
	 */
	@Override
	protected PreparedStatement prepareCommand(Connection conn) throws DatabaseException {
		try {
			preparedStatement = conn.prepareStatement(this.sqlCommand);
			preparedStatement.setInt(1, user.getUserId().getId());
			preparedStatement.setString(2, user.getName());
			
			//family name can be null
			if(user.getFamilyName() != null)
				preparedStatement.setString(3, user.getFamilyName());
			else
				preparedStatement.setString(3, "");
			
			preparedStatement.setString(4, user.getPrimaryNumber().toString());
			
			//Secondary Phone number can be null
			if(user.getSecondaryNumber() == null || 
					user.getSecondaryNumber().toString().isEmpty())
				preparedStatement.setString(5, "");
			else
				preparedStatement.setString(5, user.getSecondaryNumber().toString());
			
			preparedStatement.setString(6, user.getPrimaryEmail().getText());
			
			//Secondary Email can be null 
			if(user.getSecondaryEmail() == null ||
					user.getSecondaryEmail().getText().isEmpty())
				preparedStatement.setString(7, "");
			else
				preparedStatement.setString(7, user.getSecondaryEmail().getText());
			
			preparedStatement.setInt(8, 0);
			preparedStatement.setString(9, user.getAddress().getCountry());
			preparedStatement.setString(10, user.getAddress().getState());
			preparedStatement.setString(11, user.getAddress().getCity());
			preparedStatement.setString(12, user.getAddress().getZip());
			preparedStatement.setString(13, user.getAddress().getStreet());
			return preparedStatement;
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}
	}

}
