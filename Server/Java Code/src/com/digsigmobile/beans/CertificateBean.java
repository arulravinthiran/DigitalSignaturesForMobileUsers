package com.digsigmobile.beans;

import java.io.Serializable;

import com.digsigmobile.datatypes.CertificateId;
import com.digsigmobile.datatypes.UserId;

public class CertificateBean implements Serializable{
	
	/**
	 * to make sure that this bean is sent perfectly via sockets by streams, 
	 * this class implements serializable interface.
	 * Below is the default instance variable.
	 */
	
	private static final long serialVersionUID = 1L;
	
	private UserId			userId = null;
	private CertificateId	certificateId = null;
	private byte[]			certificateFile = null;
	
	public UserId getUserId() {
		return userId;
	}
	public void setUserId(UserId userId) {
		this.userId = userId;
	}
	public CertificateId getCertificateId() {
		return certificateId;
	}
	public void setCertificateId(CertificateId certificateId) {
		this.certificateId = certificateId;
	}
	public byte[] getCertificateFile() {
		return certificateFile;
	}
	public void setCertificateFile(byte[] certificateFile) {
		this.certificateFile = certificateFile;
	}
}
