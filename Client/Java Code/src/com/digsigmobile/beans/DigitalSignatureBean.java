package com.digsigmobile.beans;

import java.io.Serializable;
import java.sql.Timestamp;

import com.digsigmobile.datatypes.TrustCode;
import com.digsigmobile.datatypes.UserId;

public class DigitalSignatureBean implements Serializable
{
	/**
	 * serialVersionUID of Serializable interface
	 */
	private static final long serialVersionUID = 1L;
	
	private UserId			userId;
	private TrustCode		trustCode;
	private byte[]			signedFile;
	private boolean			hasSigned,isValid;
	private String          reasonForSigning;
	private Timestamp       signedTime;
	
	public Timestamp getSignedTime()
	{
		return signedTime;
	}
	public void setSignedTime(Timestamp signedTime)
	{
		this.signedTime = signedTime;
	}
	public UserId getUserId() {
		return userId;
	}
	public void setUserId(UserId userId) {
		this.userId = userId;
	}
	public TrustCode getTrustCode() {
		return trustCode;
	}
	public void setTrustCode(TrustCode trustCode) {
		this.trustCode = trustCode;
	}
	public byte[] getSignedFile() {
		return signedFile;
	}
	public void setSignedFile(byte[] signedFile) {
		this.signedFile = signedFile;
	}
	public boolean hasSigned() {
		return hasSigned;
	}
	public void setHasSigned(boolean hasSigned) {
		this.hasSigned = hasSigned;
	}
	public boolean isValid() {
		return isValid;
	}
	public void setIsValid(boolean isValid) {
		this.isValid = isValid;
	}
	public  String getSigningReason() {
		return reasonForSigning;
	}
	public void setSigningReason(String signingReason) {
		this.reasonForSigning = signingReason;
	}
}
