package com.digsigmobile.control;

import java.io.Serializable;
import java.security.KeyPair;

import com.digsigmobile.datatypes.EmailAddress;
import com.digsigmobile.socket.client.SocketManager;
import com.digsigmobile.socket.client.interfaces.ResponseListener;
import com.digsigmobile.socket.client.interfaces.UICallback;
import com.digsigmobile.socket.message.CertificateCreationRequest;
import com.digsigmobile.socket.message.KeysValidationRequest;
import com.digsigmobile.socket.message.SocketMessage;
import com.digsigmobile.socket.message.SocketMessageType;
import com.digsigmobile.util.RSA;

/**
 * method creates certificate for a user and returns the certificate id for the 
 * created certificate
 * -1 error if user is not a member
 * -2 error if user has a certificate already
 * -3 error if user has entered an incorrect Email key, which is keyP1
 * -4 error if user has entered an incorrect mobile key, which is keyP2
 * 0 if user certificate is created successfully
 * @param keyP1
 * @param keyP2
 * @param secretS
 * @return status
 */
public class CertificateCreation implements SocketStringConstants, ResponseListener {
	
	/**
	 * Delegate interfaces
	 */
	private SocketManager socketManager = null;
	private UICallback ui;
	
	/**
	 * Class variables
	 */
	private String keyP1;
	private String keyP2;
	private String secret;
	private EmailAddress email;
	
	public CertificateCreation(UICallback ui) {
		// set up socket manager
		socketManager = SocketManager.getInstance("localhost", 6000, this, null);
		this.ui = ui;
	}
	
	public void createCertificate(String keyP1, String keyP2, String secretS, EmailAddress email) {
			// Initialize field variables
			this.keyP1 = keyP1;
			this.keyP2 = keyP2;
			this.email = email;
			this.secret = secretS;
			
			// Create a request for validation of the keys
			KeysValidationRequest keysRequest = new KeysValidationRequest();
			keysRequest.keyP1 = keyP1;
			keysRequest.keyP2 = keyP2;
			keysRequest.email = email;
			
			// validate keys
			SocketMessage action = new SocketMessage(SocketMessageType.ACTION_KEYS_VALIDATION, keysRequest);
			socketManager.sendMessage(action);
	}

	@Override
	public void onResponse(int type, Serializable content) {
		switch(type) {
		case SocketMessageType.ACK_KEYS_VALIDATION_FAILURE:
			ui.onCallback(type, content);
			break;
		case SocketMessageType.ACK_KEYS_VALIDATION_SUCCESSFUL:
			// generate keypair
			KeyPair keyPair = RSA.generateKeys(keyP1, keyP2, secret);
			
			// Create a request for certificate generation
			CertificateCreationRequest certRequest = new CertificateCreationRequest();
			certRequest.email = email;
			certRequest.publicKey = keyPair.getPublic();
			
			// Generate a certificate
			SocketMessage action = new SocketMessage(SocketMessageType.ACTION_CERTIFICATE_CREATION, certRequest);
			socketManager.sendMessage(action);
			break;
		case SocketMessageType.ACK_CERTIFICATE_CREATION_FAILURE:
			ui.onCallback(type, content);
			break;
		case SocketMessageType.ACK_CERTIFICATE_CREATION_SUCCESSFUL:
			ui.onCallback(type, content);
			break;
		default:
			ui.onCallback(type, content);
			break;
		}
	}

	@Override
	public void onServerClosed(Exception e) {
		// TODO Auto-generated method stub
		
	}

}
