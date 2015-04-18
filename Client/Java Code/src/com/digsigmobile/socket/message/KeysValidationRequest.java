package com.digsigmobile.socket.message;

import com.digsigmobile.datatypes.EmailAddress;

public class KeysValidationRequest extends Request {

	private static final long serialVersionUID = 890409875609840164L;
	
	public String keyP1;
	public String keyP2;
	public EmailAddress email;
}
