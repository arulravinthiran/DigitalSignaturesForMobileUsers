package com.digsigmobile.control;

import java.sql.Timestamp;
import java.util.Arrays;

import com.digsigmobile.beans.CertificateBean;
import com.digsigmobile.beans.DigitalSignatureBean;
import com.digsigmobile.beans.DocumentBean;
import com.digsigmobile.beans.UserBean;
import com.digsigmobile.business.CertificateBiz;
import com.digsigmobile.business.DigitalSignatureBiz;
import com.digsigmobile.business.DocumentBiz;
import com.digsigmobile.business.SignatureManager;
import com.digsigmobile.business.UserBiz;
import com.digsigmobile.datatypes.EmailAddress;
import com.digsigmobile.datatypes.UserId;
import com.digsigmobile.exceptions.DatabaseException;
import com.digsigmobile.util.DigSigMobServerUtils;

public class SignatureController {

	/**
	 * Validates the  signers and returns the public key of the initiator as the status.
	 * status -1: initiator not a registered user.
	 * status -2: initiator does not have a certificate.
	 * status -3: co-signer is not a registered user. 
	 * status -4: Exceptions
	 * @param emails List of emails of initiator and cosigners
	 * @return status
	 */
	public static int validateSigners(EmailAddress[] emails) {

		try {
			UserBiz userObj = new UserBiz();
			UserBean user = userObj.retrieveUser(emails[0]);

			// error -1 if user is not registered 
			if(user == null || user.getUserId() == null)
				return -1;

			// error -2 if user does not have a certificate already
			if(!user.hasCertificate())
				return -2;

			for(int i = 1; i < emails.length; i++) {
				//check if the co-signer is a client. Status -3 if not.
				if (emails[i] != null) {
					UserBean coSignBean = userObj.retrieveUser(emails[i]);
					if(coSignBean == null)
						return -3;
				}
			}

			// if the co-signer is a client, set the initiator's public key as the status
			DigSigMobServerUtils digServerUtilObj = new DigSigMobServerUtils();
			String prmryNo = user.getPrimaryNumber().getPhoneNumber();
			String pubKey = digServerUtilObj.generatePublicKeyE(prmryNo);
			return(Integer.parseInt(pubKey));

		} catch(DatabaseException ex) {
			ex.printStackTrace();
			return -4;
		}
	}

	/**
	 * Validates the  signers and returns the public key of the initiator as the status.
	 * status -1: invalid trust code
	 * status -2: co-signer is not a registered user
	 * status -3: co-signer has no digital certificate yet
	 * status -4: not a valid co-signer for the trust code
	 * status -5: file provided does not match the original file 
	 * status -6: Exceptions
	 * @param trustCode
	 * @param email
	 * @param fileToSign
	 * @return status publicKey of initiator, if succeed
	 */
	public static int validateCoSigner(DocumentBean document, EmailAddress email) {

		try {
			DocumentBiz docBizObj = new DocumentBiz();
            
			//status = -1 for an invalid trust code
			DocumentBean docBeanObj = docBizObj.retrieveDocument(document.getTrustCode());
			if(docBeanObj == null)
				return -1;
			
			UserBiz userBizObj = new UserBiz();
			UserBean user = userBizObj.retrieveUser(email);
			UserId userId = user.getUserId();
			
			//status = -2 if invalid user
			if(user == null || userId == null)
				return -2;

			//status = -3 if user has no digital certificate
			if(!user.hasCertificate())
				return -3;
			
			DigitalSignatureBiz digSigObj = new DigitalSignatureBiz();
			boolean isCoSigner = digSigObj.validateCoSigner(document.getTrustCode(), userId);

			//status = -4 if not a valid Co-Signer for the trust code
			if(!isCoSigner)
				return -4;

			/* status = -5 if the file to be signed by the co-signer does not    
					match with the original file of the initiator. */
			if(!Arrays.equals(docBeanObj.getDocumentFile(),document.getDocumentFile()))
				return -5;

			// status is set to the co-signer's public key.
			DigSigMobServerUtils digSigUtilObj = new DigSigMobServerUtils();
			String prmryMobNo = user.getPrimaryNumber().getPhoneNumber();
			String pubKey = digSigUtilObj.generatePublicKeyE(prmryMobNo);
			return (Integer.parseInt(pubKey));

		} catch(DatabaseException dbe) {
			dbe.printStackTrace();
			return -6;
		}
	}

	public static int createSignature(DigitalSignatureBean signature, 
			boolean isInitiator, EmailAddress[] emails) {

		try {
			DocumentBiz docBiz = new DocumentBiz();
			DocumentBean document = docBiz.retrieveDocument(signature.getTrustCode());

			// verifies if trust code actually exists in db
			if (document == null || document.getTrustCode() == null)
				return -1;	// error: document doesn't exist

			// retrieve the user certificate for emails[0]
			UserBiz userBiz = new UserBiz();
			UserBean user = userBiz.retrieveUser(emails[0]);

			if (user == null)
				return -2;	// error: user doesn't exist

			// Set user id
			signature.setUserId(user.getUserId());

			CertificateBiz certBiz = new CertificateBiz();
			CertificateBean certificate = certBiz.retrieveCertificate(user.getUserId());

			if (certificate == null)
				return -3;	// error: certificate for this user doesn't exist

			// verify the signature
			if (signature == null || signature.getSignedFile() == null)
				return -4; // error: no file provided

			SignatureManager sigManager = new SignatureManager();
			if (sigManager.verifySignature(certificate.getCertificateFile(), 
					signature.getSignedFile(), document.getDocumentFile())) {

				if (isInitiator) {
					// add signature to db
					signature.setHasSigned(true);
					signature.setIsValid(true);
					signature.setSignedTime(new Timestamp(System.currentTimeMillis()));
					DigitalSignatureBiz sigBiz = new DigitalSignatureBiz();
					sigBiz.insertSigner(signature);
					
					// add placeholder signatures for the cosigners
					for (int i = 1; i < emails.length; i++) {
						UserBean coSigner = userBiz.retrieveUser(emails[i]);
						
						if (coSigner != null && coSigner.getUserId() != null) {
							DigitalSignatureBean  signaturePlaceholder = new DigitalSignatureBean();
							signaturePlaceholder.setHasSigned(false);
							signaturePlaceholder.setSigningReason("Not Applicable");
							signaturePlaceholder.setTrustCode(document.getTrustCode());
							signaturePlaceholder.setUserId(coSigner.getUserId());
							sigBiz.insertSigner(signaturePlaceholder);
						}
					}
				} else {
					// update cosigner signature on db (replace placeholder)
					signature.setHasSigned(true);
					signature.setIsValid(true);
					signature.setSignedTime(new Timestamp(System.currentTimeMillis()));
					DigitalSignatureBiz sigBiz = new DigitalSignatureBiz();
					sigBiz.updateSignatureParameters(user.getUserId(), document.getTrustCode(), 
							true, true, signature.getSigningReason(), signature.getSignedFile());
				}
				
				// send email to users
				DigSigMobServerUtils utilsObj = new DigSigMobServerUtils();
				if (isInitiator)
					utilsObj.sendMail(document.getTrustCode(), emails[0], emails[1], isInitiator, true);
				else
					utilsObj.sendMail(document.getTrustCode(), null, emails[0], isInitiator, true);
				return 0;
			}
			else {
				// send email to users
				DigSigMobServerUtils utilsObj = new DigSigMobServerUtils();
				if (isInitiator)
					utilsObj.sendMail(document.getTrustCode(), emails[0], emails[1], isInitiator, false);
				else
					utilsObj.sendMail(document.getTrustCode(), null, emails[0], isInitiator, false);
				
				return -5;	// error: signature invalid
			}
				

		} catch (Exception e) {
			e.printStackTrace();
			return -6;	// error: database exception

		}
	}

}
