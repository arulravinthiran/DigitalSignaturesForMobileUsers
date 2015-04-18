package com.digsigmobile.beans;

import java.io.Serializable;

import com.digsigmobile.datatypes.Address;
import com.digsigmobile.datatypes.EmailAddress;
import com.digsigmobile.datatypes.PhoneNumber;
import com.digsigmobile.datatypes.UserId;

public class UserBean implements Serializable
{
	/**
	 * serialVersionUID of Serializable interface
	 */
	private static final long serialVersionUID = 1L;
	
	private UserId	 		userId= null;
	private String 			name = null, familyName = null;
	private PhoneNumber 	primaryNumber = null, secondaryNumber = null;
	private EmailAddress 	primaryEmail = null, secondaryEmail = null;
	private boolean			hasCertificate = false;
	private Address 		address = null;
	
	public UserId getUserId() {
		return userId;
	}
	public void setUserId(UserId userId) {
		this.userId = userId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getFamilyName() {
		return familyName;
	}
	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	public PhoneNumber getPrimaryNumber() {
		return primaryNumber;
	}
	public void setPrimaryNumber(PhoneNumber primaryNumber) {
		this.primaryNumber = primaryNumber;
	}
	public PhoneNumber getSecondaryNumber() {
		return secondaryNumber;
	}
	public void setSecondaryNumber(PhoneNumber secondaryNumber) {
		this.secondaryNumber = secondaryNumber;
	}
	public EmailAddress getPrimaryEmail() {
		return primaryEmail;
	}
	public void setPrimaryEmail(EmailAddress primaryEmail) {
		this.primaryEmail = primaryEmail;
	}
	public EmailAddress getSecondaryEmail() {
		return secondaryEmail;
	}
	public void setSecondaryEmail(EmailAddress secondaryEmail) {
		this.secondaryEmail = secondaryEmail;
	}
	public boolean hasCertificate() {
		return hasCertificate;
	}
	public void setHasCertificate(boolean hasCertificate) {
		this.hasCertificate = hasCertificate;
	}
}