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
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.digsigmobile.beans.DocumentBean;
import com.digsigmobile.control.SignatureCreationOfCoSigner;
import com.digsigmobile.datatypes.EmailAddress;
import com.digsigmobile.datatypes.TrustCode;
import com.digsigmobile.exceptions.InvalidInputException;
import com.digsigmobile.socket.client.interfaces.UICallback;
import com.digsigmobile.socket.message.SocketMessageType;


@SuppressWarnings("restriction")
public class CoSigner extends JFrame implements UICallback {

	/**
	 * default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField txtTrustCode;
	private JTextField txtPrimaryEmail;
	private JPasswordField pwdSecret;
	private JComboBox<Object> ddlSigningReason;
	private JCheckBox chkFileUploaded;
	private JLabel lblFileUpload;
	private static CoSigner frame;
	//set this boolean to true when file uploaded by the user
	boolean isFileUploaded = false;
	
	private DocumentBean docBnObj = null;
	
	private TrustCode trCodeObj = null;

	/**
	 * Launch the application.
	 */

	public static void createAndShowCoSigner()
	{
		try {
			frame = new CoSigner();
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * Create the frame.
	 */
	public CoSigner() 
	{
		setTitle("DigSigMobile Co-Signer");
		
		
		setResizable(false);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblWelcomeToDigsigmobile = new JLabel("Welcome to DigSigMobile");
		lblWelcomeToDigsigmobile.setForeground(Color.BLUE);
		lblWelcomeToDigsigmobile.setBounds(131, 0, 144, 14);
		contentPane.add(lblWelcomeToDigsigmobile);
		
		JLabel lblTrustCode = new JLabel("Trust Code");
		lblTrustCode.setBounds(153, 21, 74, 14);
		contentPane.add(lblTrustCode);
		
		txtTrustCode = new JTextField();
		txtTrustCode.setBounds(153, 36, 86, 20);
		contentPane.add(txtTrustCode);
		txtTrustCode.setColumns(10);
		
		JLabel lblFileToSign = new JLabel("File to Sign (Maximum size: 8MB)");
		lblFileToSign.setBounds(153, 64, 210, 14);
		contentPane.add(lblFileToSign);
		
		JButton btnUpload = new JButton("Choose");
		btnUpload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				UIUtil utilObj = new UIUtil();
				docBnObj = utilObj.fileChooserHelper();
				
				if(docBnObj != null)
				{
					chkFileUploaded.setSelected(true);
	                lblFileUpload.setForeground(Color.BLUE);
	                lblFileUpload.setText(docBnObj.getFileName());
	                
	              //validate the file uploaded by the user
					isFileUploaded = true; // set this to true when all is done with the file selected
	              
				}
				
			}
		});
		btnUpload.setBounds(153, 78, 89, 23);
		contentPane.add(btnUpload);
		
		JLabel lblPrimaryEmailAddress = new JLabel("Primary Email Address");
		lblPrimaryEmailAddress.setBounds(153, 199, 155, 14);
		contentPane.add(lblPrimaryEmailAddress);
		
		txtPrimaryEmail = new JTextField();
		txtPrimaryEmail.setBounds(153, 217, 210, 20);
		contentPane.add(txtPrimaryEmail);
		txtPrimaryEmail.setColumns(10);
		
		JLabel lblSecretSentence = new JLabel("Secret Sentence");
		lblSecretSentence.setBounds(153, 112, 127, 14);
		contentPane.add(lblSecretSentence);
		
		pwdSecret = new JPasswordField();
		pwdSecret.setBackground(Color.LIGHT_GRAY);
		pwdSecret.setBounds(153, 132, 210, 20);
		contentPane.add(pwdSecret);
		
		ddlSigningReason = new JComboBox<Object>();
		ddlSigningReason.setModel(new DefaultComboBoxModel(new String[] {"------Select the reason for signing------", "I am the author.", "I am co-signing.", "I agree to the contents."}));
		ddlSigningReason.setBounds(153, 168, 210, 20);
		contentPane.add(ddlSigningReason);
		
		JLabel lblReasonForSigning = new JLabel("Reason for signing");
		lblReasonForSigning.setBounds(153, 153, 210, 14);
		contentPane.add(lblReasonForSigning);
		
		JButton btnSign = new JButton("Sign");
		btnSign.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				char[] secret = pwdSecret.getPassword();
				String secretSentence = new String(secret); 
				String email = txtPrimaryEmail.getText();
				String trustCode = txtTrustCode.getText();
				String signingReason = ddlSigningReason.getSelectedItem().toString();
				EmailAddress emailCoSigner = null;
				
				int proceed = 0;
				
				if(isFileUploaded == false)
				{
					proceed = -1;
					UIUtil.showErrorBox(rootPane, "Please choose the file to sign!");
				}
				if(proceed != -1 && trustCode.isEmpty())
				{
					proceed = -1;
					UIUtil.showErrorBox(rootPane, "Please enter the Trust code!");
				}
				else
				{
					try
					{
						if(proceed != -1)
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
				    	UIUtil.showErrorBox(rootPane, "Sorry! Server error! Please contact the service" +
				    			" team!");
				    			
				    	ex.printStackTrace();
				    }
				    
				    if(proceed != -1 && email.isEmpty())
				    {
				    	proceed = -1;
				    	UIUtil.showErrorBox(rootPane, "Please enter the Primary Email Address!");
				    }
				    else
				    {
				    	try
				    	{
				    		if(proceed != -1)
				    			emailCoSigner = new EmailAddress(email);
				    	}
				    	catch(InvalidInputException inEx)
						{
							proceed = -1;
					    	UIUtil.showErrorBox(rootPane, "Invalid format in Primary Email " +
					    			"Address!");
						}
				    	
				    	if(proceed != -1 && secretSentence.isEmpty())
						{
							proceed = -1;
					    	UIUtil.showErrorBox(rootPane, "Please enter the secret Sentence!");
						}
				    	else
				    	{
				    		if(proceed != -1 && signingReason.equals("--Select the reason for signing--"))
				    		{
				    			proceed = -1;
						    	UIUtil.showErrorBox(rootPane, "Please select the signing reason!");
				    		}
				    		
				    		else
				    		{
				    			if(proceed != -1 && isFileUploaded)
								{
									int option = UIUtil.showConfirmationBox(rootPane, 
											"Please confirm all the details!"
											, "Are you sure that all the details entered" +
										 " are valid?");
									if(option != 0)
									{
										proceed = -1;
										UIUtil.showWarningBox(rootPane, "Warning", 
												"Please correct the details!");
										
									}
									else
									{
										//send the fields to the control class
										if(proceed != -1 && isFileUploaded == true)
										{
											docBnObj.setTrustCode(trCodeObj);
											frame.dispose();
											Home.disableButtons(true);
											DigSigProgressBar.showProgressGUI("Digital Signature Creation",true);
											SignatureCreationOfCoSigner sigCoSigObj = new SignatureCreationOfCoSigner(CoSigner.this);
											sigCoSigObj.createCoSignerSignature(trCodeObj, emailCoSigner, docBnObj, secretSentence, signingReason);
											
										}
									}
									
								}
				    			else if(proceed != -1 && isFileUploaded == false)
				    			{
				    				proceed = -1;
									UIUtil.showErrorBox(rootPane, "Please upload the file to be signed!");
				    				
				    			}
				    		}
				    	}
				    }
				}
			}
		});
		btnSign.setBounds(163, 238, 89, 23);
		contentPane.add(btnSign);
		
		chkFileUploaded = new JCheckBox("");
		chkFileUploaded.setEnabled(false);
		chkFileUploaded.setBounds(248, 78, 21, 23);
		contentPane.add(chkFileUploaded);
		
		lblFileUpload = new JLabel("File not uploaded!");
		lblFileUpload.setForeground(Color.RED);
		lblFileUpload.setBounds(275, 82, 104, 14);
		contentPane.add(lblFileUpload);
		
		JButton btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				int option = UIUtil.showConfirmationBox(rootPane, "Confirm your action!"
						, "Are you sure that you want to leave the signature creation?");
					if(option == 0)
					{
						frame.dispose();
					}
				
			}
		});
		btnClose.setForeground(Color.RED);
		btnClose.setBounds(357, 17, 66, 23);
		contentPane.add(btnClose);
	}

	@Override
	public void onCallback(int type, Serializable content) {
		DigSigProgressBar.showProgressGUI("",false);
		Home.disableButtons(false);
		int status;
		switch(type) {
		case SocketMessageType.ACK_COSIGNER_VALIDATION_FAILURE:
			status = (Integer) content;
			switch(status) {
			case -1:
				UIUtil.showErrorBox(rootPane, 
						"Please enter the trust code received in your " +
						"registered primary Email address!");
				break;
			case -2:
				UIUtil.showErrorBox(rootPane, 
						"Our records indicate that the Email Address " +
						"entered does not match! Please enter your" +
						"registered Primary Email Address!");
				break;
			case -3:
				UIUtil.showErrorBox(rootPane, 
						"Our records indicate that you do not have a digital certificate! " +
						"Use the received SMS and mobile keys and create your" +
						"digital certificate to sign online!");
				break;	
			case -4:
				UIUtil.showErrorBox(rootPane, 
						"Our records indicate that the entered trust " +
						"code and Email address do not match! " +
						"Please confirm all the entered fields!");
				break;
			case -5:
				UIUtil.showErrorBox(rootPane, 
						"Our records indicate that the file " +
						"uploaded does not match the trust code!" +
						"  Please sign the file received in your " +
						"registered primary Email address!");
				break;
			case -6:
				UIUtil.showErrorBox(rootPane, 
						"Sorry! Your signature does not validate against " +
						"your public key in our records!");
				int option = UIUtil.showConfirmationBox(rootPane, 
						"Try again?",
						"Do you wish to create " + 
						"a new signature with the right secret sentence?");
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
		case SocketMessageType.ACK_SIGNATURE_CREATION_FAILURE:
			status = (Integer) content;
			int option;
			switch (status) {
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
		case SocketMessageType.ACK_SIGNATURE_CREATION_SUCCESSFUL:
			UIUtil.showInformationBox(rootPane, "Signature " +
					"Creation" +
					" successful!" ,"Congratulations! Your " +
							"signature is created " +
							"now! Use the trust code to verify " +
							"the signature status!" );
			break;
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
