package com.digsigmobile.control;

import com.digsigmobile.beans.UserBean;
import com.digsigmobile.business.UserBiz;
import com.digsigmobile.datatypes.UserId;
import com.digsigmobile.exceptions.DatabaseException;
import com.digsigmobile.exceptions.InvalidInputException;
import com.digsigmobile.util.DigSigMobServerUtils;
import com.digsigmobile.util.Messages;

public class UserController implements Messages {

	/**
	 * method registers a new user and returns its id or -1 in case of error
	 * @param user
	 * @return userId
	 * @throws InvalidInputException 
	 */
	public static int registerUser(UserBean user)  {
		
		try {
			UserBiz regUser = new UserBiz();
			UserId userId = regUser.createUser(user);
			if(userId != null) {
				DigSigMobServerUtils digServerUtilObj = new DigSigMobServerUtils();
				String pubKey = digServerUtilObj.generatePublicKeyE(user.getPrimaryNumber().
						getPhoneNumber());
				String emailKey = pubKey.substring(0, 4);
				String mobileKey = pubKey.substring(4, 8);
				
				String emailMsg =  EMAILMESSAGE + emailKey;
				String smsMsg = SMSMESSAGE + mobileKey;
				
				digServerUtilObj.sendEmail(user.getPrimaryEmail().toString(), emailMsg, 
						EMAILSUBJECT);
				digServerUtilObj.sendSMS(user.getPrimaryNumber().getPhoneNumber(), smsMsg);
				return userId.getId();
			}
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		catch (InvalidInputException e) {
			e.printStackTrace();
			return -2; // -2 signifies duplicate user
		}
		return -1;
	}
	
}
