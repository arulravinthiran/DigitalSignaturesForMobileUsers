package com.digsigmobile.util;

public interface Messages 
{
	String SMSMESSAGE = "Greetings! DigSigMobile welcomes you! " +
			"Enter the keys in the application to generate your certificate. Your SMS key to " +
			"generate a digital certificate is ";
	
	String EMAILMESSAGE = "Greetings! DigSigMobile welcomes you! Create your digital signatures now! " +
			"Say Good Bye to paper! You will receive a key as an SMS in your registered phone number " +
			"shortly. Use the keys to create your Certificate. Your Email key to generate your digital" +
			"certificate is ";
	
	final String EMAILSUBJECT = "Save trees! DigSigMobile enables you to sign online! Register now!";
	
	String COSIGNERMAILSUBJECT = " requested your digital signature for the document code ";
	
	String COSIGNERMAILMESSAGE = "Greetings from DigSigMobile! A document is waiting for your signature!" +
			" Sign now! The trust code of the document sent by the signature initiator is ";
	
	String INITIATORMAILSUBJECT = "Congratulations! You have succesfully created a digital signature!";
	
	String INITIATORMESSAGE = "Greetings from DigSigMobile! The digital signature you have created now is "+
			"valid! Anyone can verify its authenticity by using your public key. A mail has been sent" +
			" to your co-signer for the signature of the same document. Please use this code for your " +
			"reference to check the signature status of your co-signer. Your trust code is ";
	
	String SELFSIGNEDMESSAGE = "Greetings from DigSigMobile! The digital signature you have created now is "+
	"valid! Anyone can verify its authenticity by using your public key. Please use this code for your " +
	"reference to check the signature details. Your trust code is ";
	
	String ERRORMAILSUBJECT = "Alert! A digital signature involving you is invalid!";
	
	String ERRORMAILMESSAGE = "DigSigMobile informs you that a digital signature involving you is " +
			"invalid! The reason might be that the signer's secret key does not match with the secret" +
			" produced during the certificate creation! Please contact the customer service of " +
			"DigSigMobile immediately! The records with us indicate that the name of the signer for the " +
			"invalid signature is  ";
	
	String COSIGNERVALIDMSG = "Greetings from DigSigMobile! The digital signature you have created is "+
	        "valid! Anyone can verify its authenticity by using your public key. Use the trust code " +
	        "to check the signature details for all signers. The trust code is ";
	
	String STATUSMESSAGE = "A signer has succesfully created the digital signature for the document " +
			"you had sent. Use the trust code for checking the details of the signature. The trust " +
			"code is ";
	
	String STATUSMAILSUB = "Congratulations! A signer has succesfully created the digital signature of " +
			"your document with code ";
	 

}

