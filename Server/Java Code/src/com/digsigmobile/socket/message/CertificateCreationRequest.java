package com.digsigmobile.socket.message;

import java.security.PublicKey;

import com.digsigmobile.datatypes.EmailAddress;

public class CertificateCreationRequest extends Request {

	private static final long serialVersionUID = -5901890352976978790L;
	
	public PublicKey publicKey;
	public EmailAddress email;
}
