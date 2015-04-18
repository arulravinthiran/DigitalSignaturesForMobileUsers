package com.digsigmobile.beans;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.activation.MimeType;

import com.digsigmobile.datatypes.TrustCode;
import com.digsigmobile.datatypes.UserId;

@SuppressWarnings("restriction")
public class DocumentBean implements Serializable
{
	/**
	 * serialVersionUID of Serializable interface
	 */
	private static final long serialVersionUID = 1L;
	
	private UserId		    userId = null;
	private TrustCode		trustCode = null;
	private MimeType		mimeType = null;
	private byte[]			documentFile = null;
	private Timestamp		uploadedTime = null;
	private String          filename;
	
	public UserId getUserId() {
		return userId;
	}
	public void setUserId(UserId userId) {
		this.userId = userId;
	}
	public void setFileName(String fileName) {
		this.filename = fileName;
	}
	public String getFileName() {
		return filename;
	}
	public TrustCode getTrustCode() {
		return trustCode;
	}
	public void setTrustCode(TrustCode trustCode) {
		this.trustCode = trustCode;
	}
	public MimeType getMimeType() {
		return mimeType;
	}
	public void setMimeType(MimeType mimeType) {
		this.mimeType = mimeType;
	}
	public byte[] getDocumentFile() {
		return documentFile;
	}
	public void setDocumentFile(byte[] documentFile) {
		this.documentFile = documentFile;
	}
	public Timestamp getUploadedTime() {
		return uploadedTime;
	}
	public void setUploadedTime(Timestamp uploadedTime) {
		this.uploadedTime = uploadedTime;
	}
	
	
}
