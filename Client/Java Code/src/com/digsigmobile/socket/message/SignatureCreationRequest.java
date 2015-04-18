package com.digsigmobile.socket.message;

import com.digsigmobile.beans.DigitalSignatureBean;
import com.digsigmobile.datatypes.EmailAddress;

public class SignatureCreationRequest extends Request {

	private static final long serialVersionUID = 2635803800055945093L;

	public DigitalSignatureBean signature;
	public boolean isInitiator;
	public EmailAddress[] emails;
	
}
