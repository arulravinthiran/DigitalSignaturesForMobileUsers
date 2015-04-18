package com.digsigmobile.control;


import java.io.Serializable;
import java.security.KeyPair;

import com.digsigmobile.beans.DigitalSignatureBean;
import com.digsigmobile.beans.DocumentBean;
import com.digsigmobile.business.SignatureManager;
import com.digsigmobile.datatypes.EmailAddress;
import com.digsigmobile.datatypes.TrustCode;
import com.digsigmobile.exceptions.InvalidInputException;
import com.digsigmobile.socket.client.SocketManager;
import com.digsigmobile.socket.client.interfaces.ResponseListener;
import com.digsigmobile.socket.client.interfaces.UICallback;
import com.digsigmobile.socket.message.CosignerValidationRequest;
import com.digsigmobile.socket.message.FileUploadRequest;
import com.digsigmobile.socket.message.SignatureCreationRequest;
import com.digsigmobile.socket.message.SocketMessage;
import com.digsigmobile.socket.message.SocketMessageType;
import com.digsigmobile.util.RSA;

/**
 * method creates digital signature of co-signer
 * error code -1: invalid trust code
 * -2: invalid user
 * -3: invalid co-signer for the entered trust code
 * -4: file to be signed by the co-signer does not match with the original file of 
 * initiator.
 * -5: signature signed by the co-signer is invalid
 * @param trCode
 * @param secretS
 * @param coSigEmail
 * @param reason
 * @param origDOc
 * @return status
 * @author Arul
 */
public class SignatureCreationOfCoSigner implements ResponseListener {
	
	/**
	 * Socket Manager
	 */
	private SocketManager socketManager = null;
	
	/**
	 * Delegate UI interface
	 */
	private UICallback ui = null;
	
	/**
	 * Class variables
	 */
	private EmailAddress email;
	private DocumentBean document;
	private String secret;
	private String reason;
	private int status;
	
	public SignatureCreationOfCoSigner(UICallback ui) {
		socketManager = SocketManager.getInstance("localhost", 6000, this, null);
		this.ui = ui;
	}
	
	public void createCoSignerSignature(TrustCode trCode, EmailAddress coSigEmail, 
			DocumentBean cosignDOc, String secretS, String reason) 
	{
		// Set trust code on document bean
		cosignDOc.setTrustCode(trCode);
		
		// Initialize filed variables
		this.email = coSigEmail;
		this.document = cosignDOc;
		this.secret = secretS;
		this.reason = reason;
		
		// Create request for co-signer validation
		CosignerValidationRequest request = new CosignerValidationRequest();
		request.document = cosignDOc;
		request.email = coSigEmail;
		
		// validate co-signer
		SocketMessage action = new SocketMessage(SocketMessageType.ACTION_COSIGNER_VALIDATION, request);
		socketManager.sendMessage(action);
		
	}

	@Override
	public void onResponse(int type, Serializable content) {
		SocketMessage action;
		switch(type) {
		case SocketMessageType.ACK_COSIGNER_VALIDATION_FAILURE:
			ui.onCallback(type, content);
			break;
		case SocketMessageType.ACK_COSIGNER_VALIDATION_SUCCESSFUL:
			status = (Integer) content;
			// if successful, upload file
			FileUploadRequest fileRequest = new FileUploadRequest();
			fileRequest.document = document;
			fileRequest.email = email;

			action = new SocketMessage(SocketMessageType.ACTION_UPLOAD_FILE, fileRequest);
			socketManager.sendMessage(action);
			break;
		case SocketMessageType.ACK_UPLOAD_FILE_FAILURE:
			ui.onCallback(type, content);
			break;
		case SocketMessageType.ACK_UPLOAD_FILE_SUCCESSFUL:
			// update docBean trust code
			try {
				TrustCode trustCode = new TrustCode((Integer) content);
				document.setTrustCode(trustCode);
			} catch (InvalidInputException e) {
				e.printStackTrace();
			}
			
			// Generate keys
			String pubKeyP1 = Integer.toString(status).substring(0, 4);
			String pubKeyP2 = Integer.toString(status).substring(4, 8);
			KeyPair rsaKeys = RSA.generateKeys(pubKeyP1, pubKeyP2, secret);
			
			// Generate signature file
			SignatureManager sigMngrObj = new SignatureManager();
			byte[] signedFile = sigMngrObj.createSignature(rsaKeys.getPrivate(), 
					document.getDocumentFile());
			
			// Set up Signature bean
			DigitalSignatureBean digSigBeanObj = new DigitalSignatureBean();
			digSigBeanObj.setSignedFile(signedFile);
			digSigBeanObj.setSigningReason(reason);
			digSigBeanObj.setTrustCode(document.getTrustCode());
			
			// Create Signature request
			SignatureCreationRequest request = new SignatureCreationRequest();
			request.emails = new EmailAddress[] {email};
			request.isInitiator = false;
			request.signature = digSigBeanObj;
			
			// Generate signature
			action = new SocketMessage(SocketMessageType.ACTION_SIGNATURE_CREATION, request);
			socketManager.sendMessage(action);
			
			break;
		case SocketMessageType.ACK_SIGNATURE_CREATION_FAILURE:
			ui.onCallback(type, content);
			break;
		case SocketMessageType.ACK_SIGNATURE_CREATION_SUCCESSFUL:
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
