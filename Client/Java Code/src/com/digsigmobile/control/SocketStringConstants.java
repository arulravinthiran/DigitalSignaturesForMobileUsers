package com.digsigmobile.control;

/**
 * string constants that are used in the socket which the server 
 * uses to navigate to the right method
 * @author Arul
 */
public interface SocketStringConstants 
{
	static final String ACTION_USER_REGISTRATION = "user registration";
	static final String ACTION_CERTIFICATE_CREATION = "create certificate";
	static final String ACTION_SIGNATURE_CREATION = "create signature";
	static final String ACTION_KEYS_VALIDATION = "validate keys";
	static final String ACTION_SIGNERS_VALIDATION = "validate signers";
	static final String ACTION_COSIGNER_VALIDATION = "validate co-signer";
	static final String ACTION_TRUSTCODE_VERIFICATION = "verify trust code";
	

}
