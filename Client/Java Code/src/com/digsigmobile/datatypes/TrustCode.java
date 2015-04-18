package com.digsigmobile.datatypes;

import java.io.Serializable;

import com.digsigmobile.exceptions.InvalidInputException;

public class TrustCode implements Serializable
{
	/**
	 * serialVersionUID of Serializable interface
	 */
	private static final long serialVersionUID = 1L;
	
	private int trustCode;

	public TrustCode(int trustCode) throws InvalidInputException {
		setTrustCode(trustCode);
	}

	public int getTrustCode() {
		return trustCode;
	}

	private void setTrustCode(int trustCode) throws InvalidInputException {
		this.trustCode = trustCode;
		if (!isValid()) throw new InvalidInputException("Not a valid TrustCode");
	}
	
	private boolean isValid() {
		if (this.trustCode >= 0) {
			return true;
		}
		return false;
	}
}
