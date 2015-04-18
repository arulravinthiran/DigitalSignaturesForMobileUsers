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
import com.digsigmobile.socket.message.FileUploadRequest;
import com.digsigmobile.socket.message.SignatureCreationRequest;
import com.digsigmobile.socket.message.SocketMessage;
import com.digsigmobile.socket.message.SocketMessageType;
import com.digsigmobile.util.RSA;


public class SignatureCreationOfInitiator implements ResponseListener {

	/**
	 * Delegate interfaces
	 */
	private SocketManager socketManager = null;
	private UICallback ui;

	/**
	 * Class variables
	 */
	private EmailAddress[] emails = null;
	private DocumentBean document = null;
	private String secret = null;
	private String reason = null;
	private int status;

	public SignatureCreationOfInitiator(SocketManager socket, UICallback ui) {
		this.socketManager = socket;
		this.socketManager.setResponseListener(this);
		this.ui = ui;
	}

	/**
	 * creates a signature and returns the status through integer values -6 to 0.
	 * status  0: initiator's signature is valid and co-signer is a registered user.
	 * status -1: initiator and co-signer email addresses match.           
	 * status -2: initiator not a registered user.
	 * status -3: initiator does not have a certificate.
	 * status -5: initiator's signature is invalid. 
	 * status -4, -6: Exceptions
	 * @param name
	 * @param origDoc
	 * @param email
	 * @param reqdCosig
	 * @param optCosig
	 */
	public void createSignature(EmailAddress email, DocumentBean origDocBn, 
			EmailAddress reqdCosigner, String secretS, String signingReason) {

		// Initialize field variables
		emails = new EmailAddress[] {email, reqdCosigner};
		document = origDocBn;
		secret = secretS;
		reason = signingReason;

		// validate signers
		SocketMessage action = new SocketMessage(SocketMessageType.ACTION_SIGNERS_VALIDATION, emails);
		socketManager.sendMessage(action);

	}


	@Override
	public void onResponse(int type, Serializable content) {
		SocketMessage action = null;
		switch (type) {
		case SocketMessageType.ACK_SIGNERS_VALIDATION_FAILURE:
			ui.onCallback(type, content);
			break;
		case SocketMessageType.ACK_SIGNERS_VALIDATION_SUCCESSFUL:
			status = (Integer) content;
			// if successful, upload file
			FileUploadRequest fileRequest = new FileUploadRequest();
			fileRequest.document = document;
			fileRequest.email = emails[0];

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

			// create signature
			String pubKeyP1 = Integer.toString(status).substring(0, 4);
			String pubKeyP2 = Integer.toString(status).substring(4, 8);
			KeyPair rsaKeys = RSA.generateKeys(pubKeyP1, pubKeyP2, secret);

			SignatureManager signMngrObj = new SignatureManager();
			//get the signature file by passing the private key and the document
			byte[] signedFile = signMngrObj.createSignature(rsaKeys.getPrivate(), 
					document.getDocumentFile());

			DigitalSignatureBean digSigBeanObj = new DigitalSignatureBean();
			digSigBeanObj.setSignedFile(signedFile);
			digSigBeanObj.setSigningReason(reason);
			digSigBeanObj.setTrustCode(document.getTrustCode());

			SignatureCreationRequest signatureRequest = new SignatureCreationRequest();
			signatureRequest.emails = emails;
			signatureRequest.isInitiator = true;
			signatureRequest.signature = digSigBeanObj;

			action = new SocketMessage(SocketMessageType.ACTION_SIGNATURE_CREATION, signatureRequest);
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
	public void onServerClosed(Exception e) {}

}
