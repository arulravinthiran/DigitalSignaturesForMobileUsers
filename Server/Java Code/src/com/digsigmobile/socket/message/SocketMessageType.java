package com.digsigmobile.socket.message;

public interface SocketMessageType {
	// Requests from clients
	public static final int ACTION_CONNECT = 0;
	public static final int ACTION_USER_REGISTRATION = 1;
	public static final int ACTION_CERTIFICATE_CREATION = 2;
	public static final int ACTION_SIGNATURE_CREATION = 3;
	public static final int ACTION_KEYS_VALIDATION = 4;
	public static final int ACTION_SIGNERS_VALIDATION = 5;
	public static final int ACTION_COSIGNER_VALIDATION = 6;
	public static final int ACTION_TRUSTCODE_VERIFICATION = 8;
	public static final int ACTION_DISCONNECT = 9;
	public static final int ACTION_UPLOAD_FILE = 10;
	
	// Answers from server
	public static final int ACK_CONNECTION_SUCCESSFUL = 20;
	public static final int ACK_CONNECTION_FAILURE = 21;
	public static final int ACK_REGISTRATION_SUCCESSFUL = 22;
	public static final int ACK_REGISTRATION_FAILURE = 23;
	public static final int ACK_CERTIFICATE_CREATION_SUCCESSFUL = 24;
	public static final int ACK_CERTIFICATE_CREATION_FAILURE = 25;
	public static final int ACK_KEYS_VALIDATION_SUCCESSFUL = 26;
	public static final int ACK_KEYS_VALIDATION_FAILURE = 27;
	public static final int ACK_SIGNERS_VALIDATION_SUCCESSFUL = 28;
	public static final int ACK_SIGNERS_VALIDATION_FAILURE = 29;
	public static final int ACK_COSIGNER_VALIDATION_SUCCESSFUL = 30;
	public static final int ACK_COSIGNER_VALIDATION_FAILURE = 31;
	public static final int ACK_TRUSTCODE_VERIFICATION_SUCCESSFUL = 32;
	public static final int ACK_TRUSTCODE_VERIFICATION_FAILURE = 33;
	public static final int ACK_SIGNATURE_CREATION_SUCCESSFUL = 34;
	public static final int ACK_SIGNATURE_CREATION_FAILURE = 35;
	public static final int ACK_UPLOAD_FILE_SUCCESSFUL = 36;
	public static final int ACK_UPLOAD_FILE_FAILURE = 37;
	
}
