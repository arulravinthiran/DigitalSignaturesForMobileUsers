package com.digsigmobile.datatypes;

import java.io.Serializable;

import com.digsigmobile.beans.DigitalSignatureBean;
import com.digsigmobile.beans.UserBean;

public class Row implements Serializable
{
	/**
	 * default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	private UserBean user;
	private DigitalSignatureBean signature;
	
	public Row(UserBean user, DigitalSignatureBean signature)
	{
		this.user = user;
		this.signature = signature;
	}
	public UserBean getUser()
	{
		return this.user;
	}
	public DigitalSignatureBean getDigitalSignature()
	{
		return this.signature;
	}
}

