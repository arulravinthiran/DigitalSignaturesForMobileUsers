package com.digsigmobile.control;

import java.util.HashMap;
import java.util.Map;

import com.digsigmobile.business.DigitalSignatureBiz;
import com.digsigmobile.datatypes.Row;
import com.digsigmobile.datatypes.TrustCode;
import com.digsigmobile.exceptions.DatabaseException;

/**
 * class that retrieves the Map<Integer,Row>
 * @author Arul
 */
public class TrustCodeVerification 
{
	/**
	 * method returns the map returned from the DB
	 * @param trCode TrustCode
	 * @return beanObjects Map<Integer,Row>
	 * @throws DatabaseException
	 */
	public Map<Integer,Row> verifyTrustCode(TrustCode trCode) 
	 throws DatabaseException
	{
		DigitalSignatureBiz digSigBizObj = new DigitalSignatureBiz();
		 Map<Integer,Row> beanObjects = new HashMap<Integer,Row>();
		beanObjects = digSigBizObj.retrieveSignature(trCode);
		return beanObjects;
	}

}
