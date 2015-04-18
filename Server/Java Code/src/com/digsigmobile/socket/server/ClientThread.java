package com.digsigmobile.socket.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;

import com.digsigmobile.beans.UserBean;
import com.digsigmobile.control.CertificateController;
import com.digsigmobile.control.DocumentController;
import com.digsigmobile.control.SignatureController;
import com.digsigmobile.control.UserController;
import com.digsigmobile.datatypes.EmailAddress;
import com.digsigmobile.datatypes.Row;
import com.digsigmobile.datatypes.TrustCode;
import com.digsigmobile.socket.message.CertificateCreationRequest;
import com.digsigmobile.socket.message.CosignerValidationRequest;
import com.digsigmobile.socket.message.FileUploadRequest;
import com.digsigmobile.socket.message.KeysValidationRequest;
import com.digsigmobile.socket.message.SignatureCreationRequest;
import com.digsigmobile.socket.message.SocketMessage;
import com.digsigmobile.socket.message.SocketMessageType;

public class ClientThread extends Thread {
	// the server object, necessary to delegate methods
	Server server = null;
	// the socket where to listen/talk
	Socket socket;
	ObjectInputStream sInput;
	ObjectOutputStream sOutput;
	// my unique id (easier for disconnection)
	int id;
	// the only type of message this thread will receive
	SocketMessage message;
	// the date I connect
	String date;

	ClientThread(Server server, Socket socket) {
		// get server object reference
		this.server = server;
		// a unique id
		id = ++Server.uniqueId;
		this.socket = socket;
		/* Creating both Data Stream */
		System.out.println("Thread trying to create Object Input/Output Streams");
		try {
			// create output first
			sOutput = new ObjectOutputStream(socket.getOutputStream());
			sInput  = new ObjectInputStream(socket.getInputStream());
			// read the first socket message
			message = (SocketMessage) sInput.readObject();
			if (message != null) {
				SocketMessage ack = null;
				switch (message.getType()) {
				case SocketMessageType.ACTION_CONNECT:
					ack = new SocketMessage(SocketMessageType.ACK_CONNECTION_SUCCESSFUL, "");
					break;
				default: 
					ack = new SocketMessage(SocketMessageType.ACK_CONNECTION_FAILURE, "");
					break;
				}
				if (ack != null) 
					writeMsg(ack);
			}
		} catch (IOException e) {
			System.out.println("Exception creating new Input/output Streams: " + e);
			return;
		}
		// have to catch ClassNotFoundException
		catch (ClassNotFoundException e) {
			System.out.println("Exception reading SocketMessage: " + e);
		}
		date = new Date().toString() + "\n";
	}

	// what will run forever
	public void run() {
		// to loop until LOGOUT
		boolean keepGoing = true;
		while(keepGoing) {
			// read a String (which is an object)
			try {
				message = (SocketMessage) sInput.readObject();
			} catch (IOException e) {
				System.out.println(id + " Exception reading Streams: " + e);
				break;				
			} catch(ClassNotFoundException e2) {
				e2.printStackTrace();
				break;
			}
			// the content part of the SocketMessage
			Serializable content = message.getContent();

			// the answer to the client
			int result;
			SocketMessage ack = null;

			// Switch on the type of message received
			switch(message.getType()) {
			case SocketMessageType.ACTION_USER_REGISTRATION:
				System.out.println("User registration requested");
				
				// Get user registration request
				UserBean user = (UserBean) content;
				
				// User control
				result = UserController.registerUser(user);
				
				// Set up answer
				if (result >= 0)
					ack = new SocketMessage(SocketMessageType.ACK_REGISTRATION_SUCCESSFUL, result);
				else
					ack = new SocketMessage(SocketMessageType.ACK_REGISTRATION_FAILURE, result);

				// Send answer
				writeMsg(ack);
				
				break;
			case SocketMessageType.ACTION_CERTIFICATE_CREATION:
				System.out.println("Certificate creation requested");

				// Get certificate creation request
				CertificateCreationRequest certRequest = (CertificateCreationRequest) content;

				// Certificate control
				result = CertificateController.createUserCertificate(certRequest.publicKey, certRequest.email);

				// Set up answer
				if (result == 0)
					ack = new SocketMessage(SocketMessageType.ACK_CERTIFICATE_CREATION_SUCCESSFUL, result);
				else
					ack = new SocketMessage(SocketMessageType.ACK_CERTIFICATE_CREATION_FAILURE, result);

				// Send answer
				writeMsg(ack);

				break;
			case SocketMessageType.ACTION_COSIGNER_VALIDATION:
				System.out.println("Cosigner validation requested");
				
				// Get co-signer validation request
				CosignerValidationRequest cosignerRequest = (CosignerValidationRequest) content;
				
				// Signature control
				result = SignatureController.validateCoSigner(cosignerRequest.document, cosignerRequest.email);
				
				// Set up answer
				if (result >= 0)
					ack = new SocketMessage(SocketMessageType.ACK_COSIGNER_VALIDATION_SUCCESSFUL, result);
				else
					ack = new SocketMessage(SocketMessageType.ACK_COSIGNER_VALIDATION_FAILURE, result);

				// Send answer
				writeMsg(ack);
				
				break;
			case SocketMessageType.ACTION_KEYS_VALIDATION:
				System.out.println("Keys validation requested");

				// Get keys validation request
				KeysValidationRequest keysRequest = (KeysValidationRequest) content;

				// Certificate control
				result = CertificateController.validateKeys(keysRequest.keyP1, keysRequest.keyP2, keysRequest.email);

				// Set up answer
				if (result == 0)
					ack = new SocketMessage(SocketMessageType.ACK_KEYS_VALIDATION_SUCCESSFUL, result);
				else
					ack = new SocketMessage(SocketMessageType.ACK_KEYS_VALIDATION_FAILURE, result);

				// Send answer
				writeMsg(ack);

				break;
			case SocketMessageType.ACTION_SIGNATURE_CREATION:
				System.out.println("Signature creation requested");
				
				// Get signature creation request
				SignatureCreationRequest signatureRequest = (SignatureCreationRequest) content;
				
				// Signature control
				result = SignatureController.createSignature(signatureRequest.signature, 
							signatureRequest.isInitiator, signatureRequest.emails);
				
				// Set up answer
				if (result == 0)
					ack = new SocketMessage(SocketMessageType.ACK_SIGNATURE_CREATION_SUCCESSFUL, result);
				else
					ack = new SocketMessage(SocketMessageType.ACK_SIGNATURE_CREATION_FAILURE, result);

				// Send answer
				writeMsg(ack);
				
				break;
			case SocketMessageType.ACTION_SIGNERS_VALIDATION:
				System.out.println("Signers validation requested");
				
				// Get signers validation request
				EmailAddress[] emails = (EmailAddress[]) content;
				
				// Signature control
				result = SignatureController.validateSigners(emails);
				
				// Set up answer
				if (result >= 0)
					ack = new SocketMessage(SocketMessageType.ACK_SIGNERS_VALIDATION_SUCCESSFUL, result);
				else
					ack = new SocketMessage(SocketMessageType.ACK_SIGNERS_VALIDATION_FAILURE, result);

				// Send answer
				writeMsg(ack);
				
				break;
			case SocketMessageType.ACTION_TRUSTCODE_VERIFICATION:
				System.out.println("Trustcode verification requested");

				// Document control
				HashMap<Integer, Row> results = DocumentController.verifyTrustcode((TrustCode) content);

				// Set up answer
				if (results != null)
					ack = new SocketMessage(SocketMessageType.ACK_TRUSTCODE_VERIFICATION_SUCCESSFUL, results);
				else
					ack = new SocketMessage(SocketMessageType.ACK_TRUSTCODE_VERIFICATION_FAILURE, "");

				// Send answer
				writeMsg(ack);

				break;
			case SocketMessageType.ACTION_UPLOAD_FILE:
				System.out.println("File upload requested");
				
				// Get file upload request
				FileUploadRequest fileRequest = (FileUploadRequest) content;
				
				// Document control
				result = DocumentController.fileUpload(fileRequest.document, fileRequest.email);
				
				// Set up answer
				if (result >= 0)
					ack = new SocketMessage(SocketMessageType.ACK_UPLOAD_FILE_SUCCESSFUL, result);
				else
					ack = new SocketMessage(SocketMessageType.ACK_UPLOAD_FILE_FAILURE, result);

				// Send answer
				writeMsg(ack);
				
				break;
			case SocketMessageType.ACTION_DISCONNECT:
				System.out.println(id + " disconnected with a LOGOUT message.");
				
				// Stop loop
				keepGoing = false;
				
				break;
			default: break;
			}
		}
		// remove myself from the arrayList containing the list of the
		// connected Clients
		server.remove(id);
		close();
	}

	// try to close everything
	private void close() {
		// try to close the connection
		try {
			if(sOutput != null) sOutput.close();
		}
		catch(Exception e) {}
		try {
			if(sInput != null) sInput.close();
		}
		catch(Exception e) {};
		try {
			if(socket != null) socket.close();
		}
		catch (Exception e) {}
	}

	/*
	 * Write a message to the Client output stream
	 */
	private boolean writeMsg(Object msg) {
		// if Client is still connected send the message to it
		if(!socket.isConnected()) {
			close();
			return false;
		}
		// write the message to the stream
		try {
			sOutput.writeObject(msg);
		}
		// if an error occurs, do not abort just inform the user
		catch(IOException e) {
			System.out.println("Error sending message");
			System.out.println(e.toString());
			e.printStackTrace();
		}
		return true;
	}

	/**
	 * 
	 * Deal with business and persistence classes
	 * 
	 */
	/*
	// Create a new user in the database, using this user object
	// Return true if registration is successful
	private boolean registerUser() {
		try {
			UserBiz userBiz = new UserBiz();
			UserId id = userBiz.createUser(user);
			if (id != null) {
				user.setUserId(id);
				return true;
			}
		} catch (DatabaseException e) {
			System.out.println("Database exception: " + e.getMessage());
		}
		return false;
	}

	// Check the user in the database, using this user object
	// Return true if it exists
	private boolean verifyUser() {
		return verifyUser(user.getPrimaryEmail());
	}

	// Check the user in the database, using the email variable
	// Return true if it exists
	private boolean verifyUser(EmailAddress email) {
		try {
			UserBiz userBiz = new UserBiz();
			user = userBiz.retrieveUser(email);
			if (user != null)
				return true;
		} catch (DatabaseException e) {
			System.out.println("Database exception: " + e.getMessage());
		}
		return false;
	}

	// Retrieve a valid user certificate from the database
	// Return null if it doesn't exist
	private byte[] retrieveUserCertificate() {
		try {
			CertificateBiz certBiz = new CertificateBiz();
			CertificateBean bean = certBiz.retrieveCertificate(user.getUserId());
			if (bean != null)
				return bean.getCertificateFile();
		} catch (DatabaseException e) {
			System.out.println("Database exception: " + e.getMessage());
		}
		return null;
	}

	// Send an email to the user
	// Return true if email is sent
	private boolean sendEmail(String msg) {
		EmailProvider provider = new EmailProvider();
		provider.sendMail(user.getPrimaryEmail().getText(), "DigSigMobile Contact", msg);
		return provider.isSent();
	}
	*/
}
