package com.digsigmobile.UI;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.digsigmobile.beans.DocumentBean;
import com.digsigmobile.control.SignatureCreationOfInitiator;
import com.digsigmobile.datatypes.EmailAddress;
import com.digsigmobile.datatypes.TrustCode;
import com.digsigmobile.exceptions.InvalidInputException;
import com.digsigmobile.socket.client.SocketManager;
import com.digsigmobile.socket.client.interfaces.ConnectionListener;
import com.digsigmobile.socket.client.interfaces.ResponseListener;
import com.digsigmobile.socket.client.interfaces.UICallback;
import com.digsigmobile.socket.message.SocketMessageType;

/**
 * U I for Home 
 * @author Arul
 *
 */
@SuppressWarnings("restriction")
public class Home extends JFrame implements ResponseListener, ConnectionListener, UICallback
{
	/**
	 * default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	private JPasswordField pwdSecretSentence;
	private JTextField txtEmailAddress;
	private JTextField txtCoSignerEmail;
	private JTextField txtTrustCode;
	private JComboBox<Object> ddlSigningReason;
	private JCheckBox chkFileChoose;
	private JLabel lblFileNotChosen;
    private static JButton btnInitiateSignatures;
    private static JButton btnRedirectRegister;
    private static JButton btnRedirectToCoSign;
    private static JButton btnCreateCertificate;
    private static JButton btnVerifyTrustCode;
    private static JButton btnExit;
    
	/**
	 * Socket Manager for this client
	 */
	private static SocketManager socketManager = null;

	//private File file;
	private boolean fileUploaded = false;

	private TrustCode trCodeObj = null;
	private DocumentBean docBnObj = null;
	private EmailAddress emailObj = null;
	private EmailAddress coSigEmailObj = null;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Home frame = new Home();
					frame.setVisible(true);
					frame.setResizable(false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	private Home() 
	{
		// socket connection
		socketManager = SocketManager.getInstance("localhost", 6000, this, this);
		socketManager.connect();

		setTitle("DigSigMobile Home");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 303);
		getContentPane().setLayout(null);/*
		((JComponent) getContentPane()).setOpaque(true);*/

		JLabel lblFileToRegister = new JLabel("File to Sign (Maximum size : 8 MB)");
		lblFileToRegister.setBounds(10, 14, 229, 14);
		getContentPane().add(lblFileToRegister);

		JLabel lblSecretSentence = new JLabel("Secret Sentence");
		lblSecretSentence.setBounds(10, 58, 131, 14);
		getContentPane().add(lblSecretSentence);

		pwdSecretSentence = new JPasswordField();
		pwdSecretSentence.setBackground(Color.LIGHT_GRAY);
		pwdSecretSentence.setColumns(250);
		pwdSecretSentence.setBounds(10, 72, 218, 20);
		getContentPane().add(pwdSecretSentence);

		JLabel lblEmailAddress = new JLabel("Primary Email Address");
		lblEmailAddress.setBounds(10, 103, 186, 14);
		getContentPane().add(lblEmailAddress);

		JLabel lblRedirectToRegister = new JLabel("Not a member?");
		lblRedirectToRegister.setBounds(249, 35, 100, 14);
		getContentPane().add(lblRedirectToRegister);

		txtEmailAddress = new JTextField();
		txtEmailAddress.setBounds(10, 117, 218, 20);
		getContentPane().add(txtEmailAddress);
		txtEmailAddress.setColumns(45);

		txtCoSignerEmail = new JTextField();
		txtCoSignerEmail.setBounds(10, 160, 218, 20);
		getContentPane().add(txtCoSignerEmail);
		txtCoSignerEmail.setColumns(45);

		JLabel lblCoSignerEmail = new JLabel("Co-Signer's  Email Address (optional)");
		lblCoSignerEmail.setBounds(10, 145, 218, 14);
		getContentPane().add(lblCoSignerEmail);

		JLabel lblSigningReason = new JLabel("Reason for signing");
		lblSigningReason.setBounds(10, 180, 207, 20);
		getContentPane().add(lblSigningReason);

		ddlSigningReason = new JComboBox<Object>();
		ddlSigningReason.setModel(new DefaultComboBoxModel(new String[] {"----------Select the reason----------", "I am the author.", "I am co-signing.", "I agree to the contents."}));
		ddlSigningReason.setBounds(10, 201, 218, 20);
		getContentPane().add(ddlSigningReason);

		btnInitiateSignatures = new JButton("Start Signing");
		btnInitiateSignatures.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				char[] secret = pwdSecretSentence.getPassword();
				String secretSentence = new String(secret);
				String initiatorEmail = txtEmailAddress.getText();
				String coSignerEmail = txtCoSignerEmail.getText();
				String signingReason = ddlSigningReason.getSelectedItem().toString();
				
				int proceed = 0;
				EmailAddress emailObj = null;
				EmailAddress coSigEmailObj = null;
				
				//Email addresses should not match
				if(initiatorEmail.equals(coSignerEmail) && !initiatorEmail.isEmpty() && 
						!coSignerEmail.isEmpty())
				{
					proceed = -1;
					UIUtil.showErrorBox(rootPane, "Primary Email Address and Co-Signer Email Address " +
							"should not be the same!");
				}
				
				//file should be uploaded 
				if(proceed != -1 && fileUploaded == false)
				{
					proceed = -1;
					UIUtil.showErrorBox(rootPane, "Please upload the file to be signed!");
					
				}
				
				//secret sentence should be entered
				if(proceed != -1 && secretSentence.isEmpty())
				{
					proceed = -1;
					UIUtil.showErrorBox(rootPane, "Please enter the secret sentence!");
				}
				
				//check Initiator Email address
				if(proceed != -1)
				{
				   if(initiatorEmail.isEmpty())
					{
						proceed = -1;
						UIUtil.showErrorBox(rootPane, "Please enter the Primary Email Address!");
					}
					else
					{
						try
						{
							if(proceed != -1)
							{
								emailObj = new EmailAddress(initiatorEmail);
							}
						}
						catch(InvalidInputException ivEx)
						{
							proceed = -1;
					    	UIUtil.showErrorBox(rootPane, "Invalid Email Address format in " +
					    			" Primary Email Address!");
						} 
					}
				}
				
				//check Co-signer Email address format if it is not empty. Note that this field is optional
				if(proceed != -1)
				{
				   if(!coSignerEmail.isEmpty())
					{
						try
						{
						    coSigEmailObj = new EmailAddress(coSignerEmail);
						}
						catch(InvalidInputException ivEx)
						{
							proceed = -1;
					    	UIUtil.showErrorBox(rootPane, "Invalid Email Address format in " +
					    			"Co-Signer's Primary Email Address!");
						} 
					}
					
				}
				
				//signing reason should be selected
				if(proceed != -1 && signingReason.equals("--Select the reason--"))
				{
					proceed = -1;
					UIUtil.showErrorBox(rootPane, "Please select the reason for signing!");
				}
				
				//confirm the details and pass them to the control class. Show the status message accordingly.
                if(proceed != -1)
				{
					int option = UIUtil.showConfirmationBox(rootPane, 
						"Please confirm all the details!"
						, "Are you sure that all the details entered" +
							" are valid?");
					if(option != 0)
					{
						proceed = -1;
						UIUtil.showWarningBox(rootPane, "Warning", "Please correct the details!");
					}
					else
					{
					    //pass the fields to the control class
					    if(proceed != -1)
						{
					    	Home.disableButtons(true);
							DigSigProgressBar.showProgressGUI("Digital Signature creation", true);
							SignatureCreationOfInitiator sigCreIniObj = new 
									SignatureCreationOfInitiator(socketManager, Home.this);
							try {
								sigCreIniObj.createSignature(emailObj,docBnObj, coSigEmailObj, secretSentence, 
										signingReason);
							}
							catch(Exception ex)
							{
								ex.printStackTrace();
								UIUtil.showErrorBox(rootPane, "Sorry! Server error! Please contact the service team!");
							}
						}
					}
				}
			}
		});
		btnInitiateSignatures.setBounds(20, 228, 121, 23);
		getContentPane().add(btnInitiateSignatures);

		btnRedirectRegister = new JButton("Register as a member");
		btnRedirectRegister.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				EventQueue.invokeLater(new Runnable() 
				{
					public void run() 
					{
						Registration.createAndShowRegistrationGUI();
					}
				});
			}
		});
		btnRedirectRegister.setBounds(249, 50, 164, 23);
		getContentPane().add(btnRedirectRegister);

		JLabel lblReceivedATrust = new JLabel("Received a trust code?");
		lblReceivedATrust.setBounds(249, 145, 164, 14);
		getContentPane().add(lblReceivedATrust);

		btnRedirectToCoSign = new JButton("Co-Sign a document");
		btnRedirectToCoSign.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				EventQueue.invokeLater(new Runnable() 
				{
					public void run() 
					{
						CoSigner.createAndShowCoSigner();
					}
				});
			}
		});
		btnRedirectToCoSign.setBounds(249, 159, 164, 23);
		getContentPane().add(btnRedirectToCoSign);

		JLabel lblRedirectToCert = new JLabel("Received  keys?");
		lblRedirectToCert.setBounds(249, 82, 164, 14);
		getContentPane().add(lblRedirectToCert);

		btnCreateCertificate = new JButton("Create   Certificate");
		btnCreateCertificate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				EventQueue.invokeLater(new Runnable() 
				{
					public void run() 
					{
						CertificateRegistration.createAndShowCertRegistrationGUI();
					}
				});
			}
		});
		btnCreateCertificate.setBounds(249, 99, 164, 23);
		getContentPane().add(btnCreateCertificate);

		JLabel lblTrustCode = new JLabel("Trust Code");
		lblTrustCode.setBounds(249, 194, 77, 27);
		getContentPane().add(lblTrustCode);

		txtTrustCode = new JTextField();
		txtTrustCode.setBounds(319, 197, 86, 20);
		getContentPane().add(txtTrustCode);
		txtTrustCode.setColumns(10);

		btnVerifyTrustCode = new JButton("Verify Signature status");
		btnVerifyTrustCode.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				final String trustCode = txtTrustCode.getText();
				int proceed = 0;

				if(trustCode.isEmpty())
				{
					UIUtil.showErrorBox(rootPane, "Please enter the trust code!");
				}
				else
				{
					try
					{
						trCodeObj = new TrustCode(Integer.parseInt(trustCode));
					}
					catch(NumberFormatException nfEx)
					{
						proceed = -1;
						UIUtil.showErrorBox(rootPane, "Invalid format in Trust code! It can only be" +
								" numbers!");
						nfEx.printStackTrace();
					}
					catch(InvalidInputException inEx)
					{
						proceed = -1;
						UIUtil.showErrorBox(rootPane, "Invalid format in Trust code! It can only be" +
								" numbers!");
						inEx.printStackTrace();
					}
					catch(Exception ex)
					{
						proceed = -1;
						UIUtil.showErrorBox(rootPane, "Sorry! Server error! Please contact the " +
								" service team!");
						ex.printStackTrace();
					}

					if(proceed != -1)
					{
						//show the Messages grid
						EventQueue.invokeLater(new Runnable() 
						{
							public void run() 
							{
								//Message.main();
								Message.createAndDisplayTable(trustCode);
							}
						});

					}
				}
			}
		});
		btnVerifyTrustCode.setBounds(249, 228, 164, 23);
		getContentPane().add(btnVerifyTrustCode);

		JLabel lblWelcome = new JLabel("Welcome to DigSigMobile!   Now you can sign online!");
		lblWelcome.setForeground(Color.BLUE);
		lblWelcome.setBounds(95, 0, 318, 14);
		getContentPane().add(lblWelcome);

		JButton btnUpload = new JButton("Choose");
		btnUpload.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0)
			{
				UIUtil utilObj = new UIUtil();
				docBnObj = utilObj.fileChooserHelper();

				if(docBnObj != null)
				{
					//do the file upload code
					chkFileChoose.setVisible(true);
					chkFileChoose.setSelected(true);

					lblFileNotChosen.setVisible(true);
					lblFileNotChosen.setEnabled(true);
					lblFileNotChosen.setForeground(Color.BLUE);
					lblFileNotChosen.setText(docBnObj.getFileName());
					fileUploaded = true; //set this to true when the file is ready

				}
			}
		});
		btnUpload.setBounds(10, 33, 77, 23);
		getContentPane().add(btnUpload);

		chkFileChoose = new JCheckBox("");
		chkFileChoose.setVisible(false);
		chkFileChoose.setEnabled(false);
		chkFileChoose.setBounds(93, 35, 21, 23);
		getContentPane().add(chkFileChoose);

		lblFileNotChosen = new JLabel("File not selected!");
		lblFileNotChosen.setVisible(false);
		lblFileNotChosen.setEnabled(false);
		//lblFileNotChosen.setForeground(Color.RED);
		lblFileNotChosen.setBounds(120, 39, 119, 14);
		getContentPane().add(lblFileNotChosen);
		
		btnExit = new JButton("Quit");
		btnExit.setForeground(Color.RED);
		btnExit.setBounds(151, 228, 88, 23);
		btnExit.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				int option = UIUtil.showConfirmationBox(rootPane, "Confirm your action!"
						, "Are you sure that you want to exit the application?");
					if(option == 0)
					{
						UIUtil.showInformationBox(rootPane, "Thank You :)", "You are leaving the services " +
								"of DigSigMobile! See you soon! Good Bye!");
						System.exit(0);
					}
				
			}
		});
		getContentPane().add(btnExit);

	}

	/**
	 * method that disables all the buttons and re-enables them according to the user's action
	 * @param shouldDisable boolean that says if the buttons should be disabled
	 */
	public static void disableButtons(boolean shouldDisable)
	{
		if(shouldDisable)
		{
			btnInitiateSignatures.setEnabled(false);
			btnCreateCertificate.setEnabled(false);
			btnRedirectRegister.setEnabled(false);
			btnRedirectToCoSign.setEnabled(false);
			btnVerifyTrustCode.setEnabled(false);
			btnExit.setEnabled(false);
		}
		else
		{
			btnInitiateSignatures.setEnabled(true);
			btnCreateCertificate.setEnabled(true);
			btnRedirectRegister.setEnabled(true);
			btnRedirectToCoSign.setEnabled(true);
			btnVerifyTrustCode.setEnabled(true);
			btnExit.setEnabled(true);
		}
	}
	
	@Override
	public void onSocketConnected() {
		System.out.println("Socket connected");
		socketManager.setResponseListener(this);
		socketManager.start();
	}

	@Override
	public void onConnectionFailed() {
		System.out.println("Couldn't connect to server");
	}

	@Override
	public void onResponse(int type, Serializable content) {
		switch (type) {
		case SocketMessageType.ACK_CERTIFICATE_CREATION_FAILURE:

			break;
		case SocketMessageType.ACK_CERTIFICATE_CREATION_SUCCESSFUL:

			break;
		case SocketMessageType.ACK_CONNECTION_FAILURE:

			break;
		case SocketMessageType.ACK_CONNECTION_SUCCESSFUL:

			break;
		case SocketMessageType.ACK_COSIGNER_VALIDATION_FAILURE:

			break;
		case SocketMessageType.ACK_COSIGNER_VALIDATION_SUCCESSFUL:

			break;
		case SocketMessageType.ACK_KEYS_VALIDATION_FAILURE:

			break;
		case SocketMessageType.ACK_KEYS_VALIDATION_SUCCESSFUL:

			break;
		case SocketMessageType.ACK_REGISTRATION_FAILURE:

			break;
		case SocketMessageType.ACK_REGISTRATION_SUCCESSFUL:

			break;
		case SocketMessageType.ACK_SIGNATURE_CREATION_FAILURE:
		{
			DigSigProgressBar.showProgressGUI("", false);
			break;
		}
		case SocketMessageType.ACK_SIGNATURE_CREATION_SUCCESSFUL:
		{
			DigSigProgressBar.showProgressGUI("", false);
			break;
		}
		case SocketMessageType.ACK_SIGNERS_VALIDATION_FAILURE:

			break;
		case SocketMessageType.ACK_SIGNERS_VALIDATION_SUCCESSFUL:

			break;
		case SocketMessageType.ACK_TRUSTCODE_VERIFICATION_FAILURE:
		{
			Home.disableButtons(false);
			DigSigProgressBar.showProgressGUI("", false);
			break;
		}
			
		case SocketMessageType.ACK_TRUSTCODE_VERIFICATION_SUCCESSFUL:
		{
			Home.disableButtons(false);
			DigSigProgressBar.showProgressGUI("", false);
			break;
		}
			
		case SocketMessageType.ACK_UPLOAD_FILE_FAILURE:

			break;
		case SocketMessageType.ACK_UPLOAD_FILE_SUCCESSFUL:
			try {
				// update docBean trust code
				trCodeObj = new TrustCode((Integer) content);
				docBnObj.setTrustCode(trCodeObj);

				// start new signature creation

			} catch (InvalidInputException e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}

	}

	@Override
	public void onServerClosed(Exception e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCallback(int type, Serializable content) {
		Home.disableButtons(false);
		DigSigProgressBar.showProgressGUI("", false);
		
		int status;
		switch(type) {
		/* 
		 * Signers validation failure
		 */
		case SocketMessageType.ACK_SIGNERS_VALIDATION_FAILURE:
			status = (Integer) content;
			switch(status) {
			case -1:
				UIUtil.showErrorBox(rootPane, 
						"Primary Email Address " +
								"entered does not exist! Please register as" +
						"a new member!");
				break;
			case -2:
				UIUtil.showErrorBox(rootPane, 
						"Our records indicate that you do not have a  " +
								"digital certificate! Please create the digital" +
								" certificate! Contact the service team for " +
						"further assistance!");
				break;
			case -3:
				UIUtil.showErrorBox(rootPane, 
						"Co-Signer's Primary Email Address " +
								"does not exist! Please ask the co-signer to" +
						"register as a new member!");
				break;
			default:
				UIUtil.showErrorBox(rootPane, 
						"Sorry! Some error occurred! Please " +
						"contact the service team!");
				break;
			}
			break;
		/* 
		 * File upload failure
		 */
		case SocketMessageType.ACK_UPLOAD_FILE_FAILURE:
			status = (Integer) content;
			switch(status) {
			case -1:
				UIUtil.showErrorBox(rootPane, 
						"Sorry! This file could not be uploaded!");
				break;
			case -2:
				UIUtil.showErrorBox(rootPane, 
						"Sorry! The Trustcode you provided is not valid!");
				break;
			case -3:
				UIUtil.showErrorBox(rootPane, 
						"Sorry! The file you provided does not match "
						+ "the original file!");
				break;
			case -4:
				UIUtil.showErrorBox(rootPane, 
						"Primary Email Address " +
								"entered does not exist! Please register as" +
						"a new member!");
				break;
			default:
				UIUtil.showErrorBox(rootPane, 
						"Sorry! Some error occurred! Please " +
						"contact the service team!");
				break;
			}
			break;
		/*
		 *  Signature creation failure
		 */
		case SocketMessageType.ACK_SIGNATURE_CREATION_FAILURE:
			status = (Integer) content;
			int option;
			switch (status) {
			case -1:
				UIUtil.showErrorBox(rootPane, 
						"Sorry! File could not be uploaded! Please " +
						"contact the service team!");
				break;
			case -2:
				UIUtil.showErrorBox(rootPane, 
						"Primary Email Address " +
								"entered does not exist! Please register as" +
						"a new member!");
				break;
			case -3:
				UIUtil.showErrorBox(rootPane, 
						"Our records indicate that you do not have a  " +
								"digital certificate! Please create the digital" +
								" certificate! Contact the service team for " +
						"further assistance!");
				break;
			case -4:
				UIUtil.showErrorBox(rootPane, 
						"Sorry! Signature could not be properly generated! Please " +
						"contact the service team!");
				break;
			case -5:
				UIUtil.showErrorBox(rootPane, 
						"Sorry! Your signature does not validate against " +
						"your public key in our records!");
				option = UIUtil.showConfirmationBox(rootPane, 
						"Try again?",
						"Do you wish to create a new " + 
						"signature with the right secret sentence?");
				if(option == 0) {
					UIUtil.showWarningBox(rootPane, "Warning", 
							"Please choose the  secret sentence that " +
									"you had chosen at the time of certificate " +
									" creation! Contact the service team if you " +
							" need further assistance!");
				}
				break;
			default:
				UIUtil.showErrorBox(rootPane, 
						"Sorry! Some error occurred! Please " +
						"contact the service team!");
				break;
			}
			break;
		/*
		 *  Signature created successfully	
		 */
		case SocketMessageType.ACK_SIGNATURE_CREATION_SUCCESSFUL:
			UIUtil.showInformationBox(rootPane, "Signature " +
					"Creation Successful!", "Congratulations!" +
							" Your signature is valid! Notification is sent " +
							"to the co-signer! Use the trust code" +
					"  to verify the signature status!" );
			
			break;
		/*
		 * Default error message
		 */
		default:
			UIUtil.showErrorBox(rootPane, 
					"Sorry! Some error occurred! Please " +
					"contact the service team! " +
					"Error type:" + type +
					" Error info: " + ((Integer) content).toString());
			break;
		}
	}
}
