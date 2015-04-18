package com.digsigmobile.persistence.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.digsigmobile.beans.UserBean;
import com.digsigmobile.datatypes.Address;
import com.digsigmobile.datatypes.EmailAddress;
import com.digsigmobile.datatypes.PhoneNumber;
import com.digsigmobile.datatypes.UserId;
import com.digsigmobile.exceptions.DatabaseException;
import com.digsigmobile.exceptions.InvalidInputException;
import com.digsigmobile.persistence.Command;

public class CmdRetrieveUser extends Command {

	/**
	 * Receives an EmailAddress object to identify
	 * a user.
	 */
	private EmailAddress email = null;
	
	/**
	 * UserBean object to return the result
	 */
	private UserBean user = null;
	
	/**
	 * UserId object to identify a user
	 */
	private UserId userId = null;
	
	/**
	 * Initializes SQL command and sets up email
	 * @param email
	 */
	public CmdRetrieveUser(EmailAddress email) {
		this.email = email;
		this.sqlCommand = 
				"SELECT `UserID`, `Name`, `FamilyName`, "
				+ "`PrimaryMobileNumber`, `SecondaryMobileNumber`,"
				+ "`PrimaryEmailID`, `SecondaryEmailID`, `HasCertificate`,"
				+ "`Country`, `Province`, `City`, `ZipCode`, `Street`"
				+ "FROM `tbl_UserDetails` WHERE `PrimaryEmailID` = ?";
		this.isQuery = true;
	}
	
	/**
	 * Overloaded method that Initializes SQL command and sets up user id
	 * @param userId
	 */
	public CmdRetrieveUser(UserId userId) 
	{
		this.userId = userId;
		this.sqlCommand = 
				"SELECT  `UserID`, `Name`, `FamilyName`, "
				+ "`PrimaryMobileNumber`, `SecondaryMobileNumber`,"
				+ "`PrimaryEmailID`, `SecondaryEmailID`, `HasCertificate`,"
				+ "`Country`, `Province`, `City`, `ZipCode`, `Street`"
				+ "FROM `tbl_UserDetails` WHERE `UserID` = ?";
		this.isQuery = true;
	}
	
	/**
	 * method  retrieves the user details 
	 * and sets the user bean if rows are present
	 */
	@Override
	public void run() throws DatabaseException {
		try 
		{
			if (resultSet.next())
			 {
				user = new UserBean();
				
				user.setUserId(new UserId(resultSet.getInt(1)));
				user.setName(resultSet.getString(2));
				user.setPrimaryNumber(new PhoneNumber(resultSet.getString(4)));
				user.setPrimaryEmail(new EmailAddress(resultSet.getString(6)));
				
				//Family name can be null
				if(resultSet.getString(3) != null)
					user.setFamilyName(resultSet.getString(3));
				
				//Secondary Phone number can be null
				if (resultSet.getString(5) != null && !(resultSet.getString(5).isEmpty()))
					user.setSecondaryNumber(new PhoneNumber(resultSet.getString(5)));
				
				//Secondary Email id can be null
				if(resultSet.getString(7)!= null && !(resultSet.getString(7).isEmpty()) )
					user.setSecondaryEmail(new EmailAddress(resultSet.getString(7)));
				
				int hasCert = resultSet.getInt(8);
				if(hasCert == 1)
				    user.setHasCertificate(true);
				else
					user.setHasCertificate(false);
				
				user.setAddress(new Address
						(resultSet.getString(9), resultSet.getString(10),
						resultSet.getString(11), resultSet.getString(12),
						resultSet.getString(13)));
			  }
		     } 
		catch (SQLException e1)  
		{
			  throw new DatabaseException(e1.getMessage());
		}
		catch (InvalidInputException e) 
		{
			  throw new DatabaseException(e.getMessage());
		}
			
		finally
		{
			if (resultSet != null)
			{
				try 
				{
					resultSet.close();
				}
				catch (SQLException e1) 
				{
					throw new DatabaseException(e1.getMessage());	
				}
		     }
			if (preparedStatement != null) 
			{
				try {
					preparedStatement.close();
				} catch (SQLException e1) {
					throw new DatabaseException(e1.getMessage());	
				}
			}
			
		}
	}

	
	/**
	 * Overrides method to prepare a statement specific for
	 * retrieving a user.
	 */
	@Override
	protected PreparedStatement prepareCommand(Connection conn) throws DatabaseException 
	{
		try 
		{
			preparedStatement = conn.prepareStatement(this.sqlCommand);
			if(email != null && email.getText() != null)
			{
				preparedStatement.setString(1, email.getText());
			}
			else if(userId != null)
			{
				preparedStatement.setInt(1, userId.getId());
			}
			return preparedStatement; 
		} 
		catch (SQLException e) 
		{
			throw new DatabaseException(e.getMessage());
		}
	}

	/**
	 * Method to get the UserBean retrieved from the database
	 * @return user
	 */
	public UserBean getUser() {
		return user;
	}

}