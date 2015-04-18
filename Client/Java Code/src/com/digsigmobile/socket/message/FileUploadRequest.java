package com.digsigmobile.socket.message;

import com.digsigmobile.beans.DocumentBean;
import com.digsigmobile.datatypes.EmailAddress;

public class FileUploadRequest extends Request {

	private static final long serialVersionUID = -5471837248055119171L;

	public DocumentBean document;
	public EmailAddress email;
}
