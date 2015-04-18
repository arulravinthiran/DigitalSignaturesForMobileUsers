package com.digsigmobile.control;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.digsigmobile.beans.DocumentBean;
import com.digsigmobile.beans.UserBean;
import com.digsigmobile.business.DigitalSignatureBiz;
import com.digsigmobile.business.DocumentBiz;
import com.digsigmobile.business.UserBiz;
import com.digsigmobile.datatypes.EmailAddress;
import com.digsigmobile.datatypes.Row;
import com.digsigmobile.datatypes.TrustCode;
import com.digsigmobile.exceptions.DatabaseException;

public class DocumentController {

	/**
	 * method returns the map returned from the DB
	 * @param trustCode TrustCode
	 * @return beanObjects Map<Integer,Row>
	 */
	public static HashMap<Integer, Row> verifyTrustcode(TrustCode trustCode) {
		DigitalSignatureBiz digSigBizObj;
		try {
			digSigBizObj = new DigitalSignatureBiz();
			Map<Integer,Row> beanObjects = new HashMap<Integer,Row>();
			beanObjects = digSigBizObj.retrieveSignature(trustCode);
			return (HashMap<Integer, Row>) beanObjects;
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * method returns the trust code of the uploaded file or error message
	 * @param document
	 * @return
	 */
	public static int fileUpload(DocumentBean document, EmailAddress email) {
		try {

			if (document == null || document.getDocumentFile() == null)
				return -1;	// error: file not provided
			
			// verify if document already has a trust code
			// if it does, then it was uploaded by a co-signer
			if (document.getTrustCode() != null) {
				// check if valid trust code
				DocumentBiz docBiz = new DocumentBiz();
				DocumentBean docToCompare = docBiz.retrieveDocument(document.getTrustCode());
				if (docToCompare == null)
					return -2;	// error: not a valid trust code
				
				// verify if the files matches
				if (!Arrays.equals(document.getDocumentFile(),docToCompare.getDocumentFile()))
					return -3;	// error: provided file is different from original file
				
				// everything worked
				return document.getTrustCode().getTrustCode();

			} else {
				// there's no trust code. it was started by an initiator
				
				// add initiator's id to the file
				UserBiz userBiz = new UserBiz();
				UserBean user = userBiz.retrieveUser(email);
				if (user == null)
					return -4;	// error: user not registered
				
				// set uploader id in the document
				document.setUserId(user.getUserId());
				
				DocumentBiz docBiz = new DocumentBiz();
				TrustCode trustCode = docBiz.createDocument(document);
				
				// return new trust code
				return trustCode.getTrustCode();
			}
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
		return -5;	// error: database exception
	}

}
