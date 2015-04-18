package com.digsigmobile.UI;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.digsigmobile.control.CertificateCreation;
import com.digsigmobile.datatypes.EmailAddress;
import com.digsigmobile.exceptions.InvalidInputException;
import com.digsigmobile.socket.client.interfaces.UICallback;
import com.digsigmobile.socket.message.SocketMessageType;

@SuppressWarnings("restriction")
public class CertificateRegistration extends JFrame implements UICallback {
	
	/**
	 * default serialVersionUID;
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JPasswordField pwdSecretSentence;
    private JLabel lblWelcomeToDigsigmobile;
    private JLabel lblSecretKey;
    private JLabel lblEmailKey;
    private JLabel lblMobileKey;
    private JLabel lblEmailAddress;
    private JTextField txtEmailKey;
    private JTextField txtEmailAddress;
    private JTextField txtMobileKey;
    private JButton btnCreateCertificate;
    private JPanel pnlMain;
    private static JFrame frame; 
    private JButton btnQuit;
	
    
    /**
	 * Launch the application.
	 */
	public static void createAndShowCertRegistrationGUI()
	{
		try 
		{
			frame = new CertificateRegistration();
			frame.setVisible(true);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CertificateRegistration frame = new CertificateRegistration();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public CertificateRegistration() 
	{

		setTitle("DigSigMobile Certificate Creation");
		setResizable(false);/*
		setSize(300,300);*/
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		pnlMain = new JPanel();
		pnlMain.setBounds(100, 11, 209, 250);
		contentPane.add(pnlMain);
		pnlMain.setLayout(null);
		
		lblWelcomeToDigsigmobile = new JLabel("Welcome to DigSigMobile");
		lblWelcomeToDigsigmobile.setForeground(Color.BLUE);
		lblWelcomeToDigsigmobile.setBounds(32, 0, 157, 14);
		pnlMain.add(lblWelcomeToDigsigmobile);
		
		pwdSecretSentence = new JPasswordField();
		pwdSecretSentence.setBackground(Color.LIGHT_GRAY);
		pwdSecretSentence.setBounds(22, 56, 167, 20);
		pnlMain.add(pwdSecretSentence);
		
		lblSecretKey = new JLabel("Secret Key");
		lblSecretKey.setBounds(22, 31, 98, 14);
		pnlMain.add(lblSecretKey);
		
		lblEmailKey = new JLabel("Email Key");
		lblEmailKey.setBounds(22, 80, 70, 14);
		pnlMain.add(lblEmailKey);
		
		txtEmailKey = new JTextField();
		txtEmailKey.setBounds(21, 98, 86, 20);
		pnlMain.add(txtEmailKey);
		txtEmailKey.setColumns(10);
		
		lblMobileKey = new JLabel("Mobile Key");
		lblMobileKey.setBounds(22, 122, 70, 14);
		pnlMain.add(lblMobileKey);
		
		txtMobileKey = new JTextField();
		txtMobileKey.setBounds(22, 139, 84, 20);
		txtMobileKey.setColumns(10);
		pnlMain.add(txtMobileKey);
		
		lblEmailAddress = new JLabel("Primary Email Address");
		lblEmailAddress.setBounds(22, 163, 167, 14);
		pnlMain.add(lblEmailAddress);
		
		txtEmailAddress = new JTextField();
		txtEmailAddress.setBounds(22, 183, 167, 20);
		pnlMain.add(txtEmailAddress);
		txtEmailAddress.setColumns(10);
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		
		
		btnCreateCertificate = new JButton("Create Certificate");
		btnCreateCertificate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) 
			{
				char[] secret = pwdSecretSentence.getPassword();
				String secretSentence = new String(secret);
				String mobileKey = txtMobileKey.getText();
				String emailKey = txtEmailKey.getText();
				String emailAddress = txtEmailAddress.getText();
				EmailAddress email = null;
				int proceed = 0;
				
				if(secretSentence.isEmpty())
				{
					proceed = -1;
					UIUtil.showErrorBox(rootPane, "Please enter the secret sentence!");
				}
				else
				{
					if(proceed != -1 && mobileKey.isEmpty())
					{
						proceed = -1;
						UIUtil.showErrorBox(rootPane, "Please enter the mobile key!");
					}
					else
					{
						if(proceed != -1 && emailKey.isEmpty())
						{
							proceed = -1;
							UIUtil.showErrorBox(rootPane, "Please enter the email key!");
						}
						else
						{
							if(proceed != -1 && emailAddress.isEmpty())
							{
								proceed = -1;
								UIUtil.showErrorBox(rootPane, "Please enter the Primary Email Address!");
							}
							else
							{
								try
								{
									email = new EmailAddress(emailAddress);
								}
								catch(InvalidInputException ex)
								{
									proceed = -1;
									UIUtil.showErrorBox(rootPane, "Invalid Email Address format in " +
											"Primary Email Address!");
								}
								/**
								 * pass the values to the control class after confirming from the user.
								 */
								if(proceed != -1)
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
										/*pnlMain.setVisible(false);
										lblYourRequestIs.setVisible(true);
										progressBar.setVisible(true);
										txtAreaProgress.setVisible(true);
										pnlProgressBar.setVisible(true);*/
										frame.dispose();
										Home.disableButtons(true);
										DigSigProgressBar.showProgressGUI("Certificate Creation",
												true);
										CertificateCreation certCreObj = new CertificateCreation
										   (CertificateRegistration.this);
										certCreObj.createCertificate(emailKey, mobileKey, secretSentence, email);
										
									}
									
								}
							}
						}
					}
				}
				
			}
		});
		btnCreateCertificate.setBounds(32, 214, 137, 23);
		pnlMain.add(btnCreateCertificate);
		
		btnQuit = new JButton("Quit");
		btnQuit.setBounds(298, 226, 75, 23);
		JButton btnQuit = new JButton("Close");
		btnQuit.setForeground(new Color(255, 0, 0));
		btnQuit.setBounds(353, 11, 76, 23);
		btnQuit.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				int option = UIUtil.showConfirmationBox(rootPane, "Confirm your action!"
						, "Are you sure that you want to leave the certificate creation?");
					if(option == 0)
					{
						frame.dispose();
					}
				
			}
		});
		contentPane.add(btnQuit);
		
		
	}

	@Override
	public void onCallback(int type, Serializable content) {
		//frame.dispose();
		DigSigProgressBar.showProgressGUI("",false);
		Home.disableButtons(false);
		int status;
		switch(type) {
		/*
		 * Keys validation failure
		 */
		case SocketMessageType.ACK_KEYS_VALIDATION_FAILURE:
			status = (Integer) content;
			switch(status) {
			case -1:
				UIUtil.showErrorBox(rootPane, "Invalid Email Address!" +
						"Our records indiacate that the Email Address " +
						"entered does not match! Please enter your" +
						"registered Primary Email Address!");
				break;
			case -2:
				UIUtil.showErrorBox(rootPane, 
						"Our records indicate that you already have a  " +
								"digital certificate!");
				break;
			case -3:
				UIUtil.showErrorBox(rootPane, "Invalid Email key!" );
				break;
			case -4:
				UIUtil.showErrorBox(rootPane, "Invalid Mobile key!" );
				break;
			default:
				UIUtil.showErrorBox(rootPane, 
						"Sorry! Some error occurred! Please " +
						"contact the service team!");
				break;
			}
			break;
		/*
		 * Certificate creation failure
		 */
		case SocketMessageType.ACK_CERTIFICATE_CREATION_FAILURE:
			status = (Integer) content;
			switch(status) {
			case -1:
				UIUtil.showErrorBox(rootPane, 
						"The certificate could not be created!");
				break;
			case -2:
				UIUtil.showErrorBox(rootPane, 
						"We are having problems with our database!");
				break;
			case -3:
				UIUtil.showErrorBox(rootPane, 
						"The generated certificate is invalid!");
				break;
			case -4:
				UIUtil.showErrorBox(rootPane, 
						"The generated certificate is invalid!");
				break;
			default:
				UIUtil.showErrorBox(rootPane, 
						"Sorry! Some error occurred! Please " +
						"contact the service team!");
				break;
			}
			break;
		/*
		 * Certificate created successfully
		 */
		case SocketMessageType.ACK_CERTIFICATE_CREATION_SUCCESSFUL:
			UIUtil.showInformationBox(rootPane, "Certificate Creation" +
					" successful!" ,"Congratulations! You can sign online " +
					"now! Anyone could verify your signature!" );
			break;
		/*
		 * Default error message
		 */
		default:
			UIUtil.showErrorBox(rootPane, 
					"Sorry! Some error occurred! Please " +
					"contact the service team!" +
					"Error type:" + type +
					"Error info: " + ((Integer) content).toString());
			break;
		}
	}
	
}
