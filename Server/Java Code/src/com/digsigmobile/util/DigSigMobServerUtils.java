package com.digsigmobile.util;

import java.math.BigInteger;

import com.digsigmobile.beans.DocumentBean;
import com.digsigmobile.beans.UserBean;
import com.digsigmobile.business.DocumentBiz;
import com.digsigmobile.business.UserBiz;
import com.digsigmobile.datatypes.EmailAddress;
import com.digsigmobile.datatypes.TrustCode;
import com.digsigmobile.exceptions.DatabaseException;

/**
 * Utils class that sends email, sends sms, 
 * generates public key given a mobile number
 * @author Arul,Bernado
 */
public class DigSigMobServerUtils implements Messages
{
	public static final BigInteger R = new BigInteger("298895077120468797620027224312030"
			+ "56791124520443466841492575492085042978563057141655636145216127193482504910"
			+ "258235356540546009941");
	
	/**
	 * method generates 8 digit public key e for a given string 
	 * get a 10 digit mobile number, shuffle it and return the key.
	 * Note that the last character is either 1 or 3 or 7.
	 * @param prmryMobNo primary mobile number
	 * @return pubKeyE public key E
	 * @author Arul
	 */
	public String generatePublicKeyE(String prmryMobNo) 
	{
		String pubKeyE = "";
		int mbNoLngth = prmryMobNo.length();
		if(mbNoLngth >= 7)
		{
			String pubKeyP1 = prmryMobNo.substring(3, 7) ;
			String pubKeyP2 = prmryMobNo.substring(0, 3);
			if (!pubKeyP2.contains("1"))
					pubKeyE = pubKeyP1 + pubKeyP2 + "1"; 
			else if (!pubKeyP2.contains("3"))
				pubKeyE = pubKeyP1 + pubKeyP2 + "3";
			else if (!pubKeyP2.contains("7"))
				pubKeyE = pubKeyP1 + pubKeyP2 + "7";
			else
				pubKeyE = pubKeyP1 + pubKeyP2 + "1"; 
		}
		return pubKeyE;
		
	}
	
	/**
     * method sends email to the recipient email given the message and subject
     * @param recipientEmail
     * @param message
     * @param subject
     * @author Bernardo
     */
	public void sendEmail(String recipientEmail, String message, String subject) 
	{
		EmailProvider emailProv = new EmailProvider();
		emailProv.sendMail(recipientEmail, subject, message);
		
	}
	
	/**
	 * method sends the given message as an SMS to the given mobile number  
	 * @param mobNo
	 * @param message
	 * @author Bernardo
	 */
	public void sendSMS(String mobNo, String message)
	{
		SMSProvider smsprov = new SMSProvider();
		smsprov.sendSMS(mobNo, message);
	}
	
	/**
	 * Overloaded method sends emails to the initiator and co-signer
	 * Note that isSignValid is true for valid signatures
	 * Also note that email could be null if the method is called from the co-signer's control classes
	 * In addition, trCode could be null if the method is called for invalid signature of initiator.
	 * @param trCode
	 * @param email
	 * @param cosigEmail
	 * @param isInitiator
	 * @throws DatabaseException 
	 */
	public void sendMail(TrustCode trCode, EmailAddress email, EmailAddress cosigEmail, 
			boolean isInitiator, boolean isSignValid) throws DatabaseException 
	{
		if(isInitiator)
		{
			UserBiz userBizObj = new UserBiz();
			UserBean userBeanObj = userBizObj.retrieveUser(email);
			
			String initiator = "";
			if(userBeanObj.getFamilyName() != null)
				initiator = userBeanObj.getFamilyName()+ " " + userBeanObj.getName();
			else
				initiator = userBeanObj.getName();
			
			if(isSignValid) 
			{
				
				if(cosigEmail != null && !cosigEmail.equals(""))
				{
					//send mail to initiator
					String initMsg = INITIATORMESSAGE + Integer.toString(trCode.getTrustCode());
					String cosigMsg = COSIGNERMAILMESSAGE + Integer.toString(trCode.getTrustCode());
					this.sendEmail(email.toString(), initMsg, INITIATORMAILSUBJECT);
					
					//send mail to co-signer
					String coSigSub = initiator + COSIGNERMAILSUBJECT + Integer.toString
					 (trCode.getTrustCode());
					this.sendEmail(cosigEmail.toString(), cosigMsg, coSigSub);
				}
				
				else if(cosigEmail == null || cosigEmail.equals(""))
				{
					//send mail to self-signer
					String initMsg = SELFSIGNEDMESSAGE + Integer.toString(trCode.getTrustCode());
					this.sendEmail(email.toString(), initMsg, INITIATORMAILSUBJECT);
				}
				
			
			}
			else
			{
				String initMsg = ERRORMAILMESSAGE + initiator;
				this.sendEmail(email.toString(), initMsg, ERRORMAILSUBJECT);
			}
		}
		if (!isInitiator)
		{
			UserBiz userBizObj = new UserBiz();
			UserBean userBeanObj = userBizObj.retrieveUser(cosigEmail);
			String coSigner = userBeanObj.getFamilyName()+ " " + userBeanObj.getName();
			
			if(isSignValid)
			{
				//send mail to co-signer
				String coSigMsg = COSIGNERVALIDMSG + Integer.toString(trCode.getTrustCode());
				this.sendEmail(cosigEmail.toString(), coSigMsg, INITIATORMAILSUBJECT);
				
				
				//retrieve the initiator's emailAddress
				DocumentBiz docBizObj = new DocumentBiz();
				DocumentBean docBeanObj = docBizObj.retrieveDocument(trCode);
				userBeanObj = userBizObj.retrieveUser(docBeanObj.getUserId());
				String initEmail = userBeanObj.getPrimaryEmail().toString();
				
				//send mail to initiator as well.
				String initMsg = STATUSMESSAGE + trCode.getTrustCode();
				String subject = STATUSMAILSUB + trCode.getTrustCode();
				this.sendEmail(initEmail, initMsg, subject);
			}
			else
			{
				String coSignMsg = ERRORMAILMESSAGE + coSigner;
				this.sendEmail(cosigEmail.toString(), coSignMsg, ERRORMAILSUBJECT);
				
				//send the mail to initiator as well by retrieving the initiator email
				DocumentBiz docBizObj = new DocumentBiz();
				DocumentBean docBeanObj = docBizObj.retrieveDocument(trCode);
				userBeanObj = userBizObj.retrieveUser(docBeanObj.getUserId());
				String initEmail = userBeanObj.getPrimaryEmail().toString();
				String initMsg = ERRORMAILMESSAGE + userBeanObj.getFamilyName() + "" + 
				 userBeanObj.getName();
				this.sendEmail(initEmail, initMsg, ERRORMAILSUBJECT);
			}
			
		}
		
		

	}
	
}