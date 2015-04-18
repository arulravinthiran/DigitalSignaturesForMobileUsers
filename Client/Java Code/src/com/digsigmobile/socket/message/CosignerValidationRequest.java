package com.digsigmobile.socket.message;

import com.digsigmobile.beans.DocumentBean;
import com.digsigmobile.datatypes.EmailAddress;

public class CosignerValidationRequest extends Request{

	private static final long serialVersionUID = -1634781192510414576L;

	public DocumentBean document;
	public EmailAddress email;
}
