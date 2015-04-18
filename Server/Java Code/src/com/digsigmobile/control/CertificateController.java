package com.digsigmobile.control;

import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

import com.digsigmobile.beans.CertificateBean;
import com.digsigmobile.beans.UserBean;
import com.digsigmobile.business.CertificateBiz;
import com.digsigmobile.business.CertificateManager;
import com.digsigmobile.business.UserBiz;
import com.digsigmobile.datatypes.CertificateId;
import com.digsigmobile.datatypes.EmailAddress;
import com.digsigmobile.datatypes.UserId;
import com.digsigmobile.exceptions.DatabaseException;
import com.digsigmobile.util.DigSigMobServerUtils;

public class CertificateController {

	/**
	 * method validates the entered mobile and email keys and returns the user id 
	 * if all keys are correct
	 * error code -1: not a registered user
	 * -2: user has a certificate already
	 * -3: Email key keyP1 is incorrect
	 * -4: Mobile key keyP2 is incorrect
	 * @param keyP1
	 * @param keyP2
	 * @param email
	 * @return status
	 */
	public static int validateKeys(String keyP1, String keyP2, EmailAddress email) {

		try {

			UserBiz userObj = new UserBiz();
			int status = 1;
			UserBean user = userObj.retrieveUser(email);

			// error -1 if user is not registered 
			if(user == null)
				status = -1;
			else {
				boolean hasCertificate = user.hasCertificate();

				// error -2 if user has a certificate already
				if(hasCertificate)
					status = -2;
				else {
					DigSigMobServerUtils utilsObj = new DigSigMobServerUtils();
					String prmryPhnNo = user.getPrimaryNumber().getPhoneNumber();
					String pubKey = utilsObj.generatePublicKeyE(prmryPhnNo);

					// error -3 if user-entered key P1 is incorrect
					if (!keyP1.equals(pubKey.substring(0, 4)))
						status = -3;

					// error -4 if user-entered key P2 is incorrect
					if (!keyP2.equals(pubKey.substring(4, 8)))
						status =  -4;

					// set the user id to status if there are no errors
					if(status != -3 && status != -4)
						status = 0;
				}
			}
			return status;
		} catch(DatabaseException de) {
			de.printStackTrace();
			return -5;
		}

	}

	/**
	 * method creates a new certificate for the user and adds to
	 * the server database
	 * @param publicKey
	 * @param email
	 * @return 0 if successful
	 */
	public static int createUserCertificate(PublicKey publicKey, EmailAddress email) {

		try {

			UserBiz userBizObj = new UserBiz();
			UserBean userBnObj = userBizObj.retrieveUser(email);

			CertificateManager certMangerObj = new CertificateManager();
			X509Certificate userCert = 
					certMangerObj.createUserCertificate(publicKey, userBnObj);
			
			if(certMangerObj.verifyUserCertificate(userCert)) {
				
				byte[] certFile = userCert.getEncoded();
				UserId userId = userBnObj.getUserId();

				CertificateBean certBnObj = new CertificateBean();
				certBnObj.setUserId(userId);
				certBnObj.setCertificateFile(certFile);

				CertificateBiz certBizObj = new CertificateBiz();
				CertificateId certId = certBizObj.createCertificate(certBnObj);

				if(certId != null) {
					userBizObj.setCertificateStatus(userId, true);
					return 0;
				}
				else
					return -1;	// error: can't create certificate
			}
		} catch(DatabaseException dbEx) {
			dbEx.printStackTrace();
			return -2;	// error: database exception
		} catch(CertificateEncodingException certEnEx) {
			certEnEx.printStackTrace();
			return -3;	// error: certificate exception
		}
		return -4;	// error: invalid user certificate
	}

}
