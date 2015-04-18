package com.digsigmobile.beans;

import java.sql.Timestamp;

import com.digsigmobile.datatypes.CertificateId;
import com.digsigmobile.datatypes.RevocationId;

public class RevokedCertificateBean
{
	private RevocationId  revokdCertId = null;
	private String        revokingReason = null;
	private CertificateId certId = null;
	private Timestamp     revokedTime = null;
	
	/**
	 * @param revokdCertId the revokdCertId to set
	 */
	public void setRevokdCertId(RevocationId revokdCertId) {
		this.revokdCertId = revokdCertId;
	}
	/**
	 * @return the revokdCertId
	 */
	public RevocationId getRevokdCertId() {
		return revokdCertId;
	}
	/**
	 * @param revokingReason the revokingReason to set
	 */
	public void setRevokingReason(String revokingReason) {
		this.revokingReason = revokingReason;
	}
	/**
	 * @return the revokingReason
	 */
	public String getRevokingReason() {
		return revokingReason;
	}
	/**
	 * @param certId the certId to set
	 */
	public void setCertId(CertificateId certId) {
		this.certId = certId;
	}
	/**
	 * @return the certId
	 */
	public CertificateId getCertId() {
		return certId;
	}
	/**
	 * @param revokedTime the revokedTime to set
	 */
	public void setRevokedTime(Timestamp revokedTime) {
		this.revokedTime = revokedTime;
	}
	/**
	 * @return the revokedTime
	 */
	public Timestamp getRevokedTime() {
		return revokedTime;
	}

}
