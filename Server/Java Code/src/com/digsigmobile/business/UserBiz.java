package com.digsigmobile.business;

import com.digsigmobile.beans.UserBean;
import com.digsigmobile.datatypes.EmailAddress;
import com.digsigmobile.datatypes.UserId;
import com.digsigmobile.exceptions.DatabaseException;
import com.digsigmobile.exceptions.InvalidInputException;
import com.digsigmobile.persistence.Persistence;
import com.digsigmobile.persistence.user.CmdCreateUser;
import com.digsigmobile.persistence.user.CmdRetrieveMaxUserId;
import com.digsigmobile.persistence.user.CmdRetrieveUser;
import com.digsigmobile.persistence.user.CmdUpdateUserCertStatus;

public class UserBiz 
{
	
	/**
	 * Persistence object
	 */
	private Persistence persistence = null;
	
	public UserBiz() throws DatabaseException
	{
		this.persistence = Persistence.getInstance();
		if (!persistence.isConnected()) persistence.connect();
	}
	
	/**
	 * Receives an UserBean object without userId, creates a new
	 * id and inserts this new user in the database. Returns null
	 * or throws exception in case of error.
	 * @param user
	 * @return
	 * @throws DatabaseException
	 * @throws InvalidInputException 
	 */
	public UserId createUser(UserBean user) throws DatabaseException, InvalidInputException 
	{
		//check for a duplicate user before creating the user
		UserBean duplicateUser = retrieveUser(user.getPrimaryEmail());
		if(duplicateUser != null)
		{
			throw new InvalidInputException("User already exists! The Primary Email Address you had" +
					" entered for registration is already in use! Contact the service team for further " +
					"assistance!");
		}
		
		else
		{
			CmdRetrieveMaxUserId cmdId = new CmdRetrieveMaxUserId();
			persistence.run(cmdId);
			if (cmdId.getUserId() != null) {
				user.setUserId(cmdId.getUserId());
				CmdCreateUser command = new CmdCreateUser(user);
				persistence.run(command);
			}
			persistence.disconnect();
			return cmdId.getUserId(); 
		}
	}
	
	/**
	 * Retrieves a UserBean object or null if the user doesn't exist.
	 * @param email
	 * @return
	 * @throws DatabaseException
	 */
	public UserBean retrieveUser(EmailAddress email) throws DatabaseException 
	{
		if (email == null)
			return null;
		CmdRetrieveUser command = new CmdRetrieveUser(email);
		persistence.run(command);
		persistence.disconnect();
		return command.getUser();
	}
	
	/**
	 * Overloaded method that retrieves a UserBean object or null if the user doesn't exist.
	 * @param userId
	 * @return
	 * @throws DatabaseException
	 */
	public UserBean retrieveUser(UserId userId) throws DatabaseException 
	{
		CmdRetrieveUser command = new CmdRetrieveUser(userId);
		persistence.run(command);
		persistence.disconnect();
		return command.getUser();
	}
	
	/**
	 * Updates the user database, setting the user's certificate
	 * status to the boolean received as parameter. Throws an exception
	 * is case of error.
	 * @param userId
	 * @param status
	 * @throws DatabaseException
	 */
	public void setCertificateStatus(UserId userId, boolean status) throws DatabaseException {
		CmdUpdateUserCertStatus command = new CmdUpdateUserCertStatus(userId, status);
		persistence.run(command);
		persistence.disconnect();
	}
}
