package com.digsigmobile.UI;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.Hashtable;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.digsigmobile.beans.UserBean;
import com.digsigmobile.control.UserRegistration;
import com.digsigmobile.datatypes.Address;
import com.digsigmobile.datatypes.EmailAddress;
import com.digsigmobile.datatypes.PhoneNumber;
import com.digsigmobile.exceptions.InvalidInputException;
import com.digsigmobile.socket.client.interfaces.UICallback;
import com.digsigmobile.socket.message.SocketMessageType;

/**
 * U I for Registration 
 * @author Arul
 */
@SuppressWarnings("restriction")
public class Registration extends JFrame implements UICallback
{

	/**
	 * default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	private JTextField txtName;
	private JTextField txtPrimaryMobile;
	private JTextField txtPrimaryEmail;
	private JTextField txtCity;
	private JTextField txtFamilyName;
	private JTextField txtSecondaryMobile;
	private JTextField txtSecondaryEmailAddress;
	private JTextField txtHomeAddress;
	private JTextField txtPostalCode;
	private JComboBox<Object> ddlProvince;
	private JComboBox<Object> ddlCountry;
	private Hashtable<String, String[]> provinces = new Hashtable<String, String[]>();
	private JTextField txtPrmryMobNoCode;
	private JTextField txtSecMobNoCode;
	private static Registration frame;
	/**
	 * Launch the application.
	 */
	public static void createAndShowRegistrationGUI()
	{
	
		try 
		{
			frame = new Registration();
			frame.setVisible(true);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
	}
	/**
	 * Create the frame.
	 */
	public Registration() 
	{
		String[] items = { "--Select--", "Brazil", "Canada", "India", "USA"};
		
		setTitle("DigSigMobile Registration");
		setResizable(false);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblName = new JLabel("Name ");
		lblName.setBounds(27, 21, 38, 14);
		contentPane.add(lblName);
		
		txtName = new JTextField();
		txtName.setFont(new Font("Tahoma", Font.PLAIN, 11));
		txtName.setBackground(Color.WHITE);
		txtName.setForeground(Color.BLACK);
		txtName.setBounds(27, 43, 170, 20);
		contentPane.add(txtName);
		txtName.setColumns(10);
		
		JLabel lblPrimaryMobile = new JLabel("Primary Mobile Number");
		lblPrimaryMobile.setBounds(27, 63, 135, 14);
		contentPane.add(lblPrimaryMobile);
		
		txtPrimaryMobile = new JTextField();
		txtPrimaryMobile.setBounds(69, 83, 128, 20);
		contentPane.add(txtPrimaryMobile);
		txtPrimaryMobile.setColumns(10);
		
		JLabel lblPrimaryEmailAddress = new JLabel("Primary Email Address ");
		lblPrimaryEmailAddress.setBounds(27, 105, 135, 14);
		contentPane.add(lblPrimaryEmailAddress);
		
		txtPrimaryEmail = new JTextField();
		txtPrimaryEmail.setBounds(27, 127, 170, 20);
		contentPane.add(txtPrimaryEmail);
		txtPrimaryEmail.setColumns(10);
		
		JLabel lblHomeAddress = new JLabel("Residential Address ");
		lblHomeAddress.setBounds(27, 155, 120, 14);
		contentPane.add(lblHomeAddress);
		
		txtCity = new JTextField();
		txtCity.setBounds(27, 211, 170, 20);
		contentPane.add(txtCity);
		txtCity.setColumns(10);
		
		JLabel lblOptional = new JLabel("* Required Field");
		lblOptional.setForeground(Color.RED);
		lblOptional.setBounds(27, 242, 110, 14);
		contentPane.add(lblOptional);
		
		JButton btnRegister = new JButton("Register");
		btnRegister.addActionListener(new ActionListener() 
		{
			/**
			 * DO NOT MODIFY THIS COMPLEX IF-ELSE LADDER.
			 * IT VALIDATES THE USER ENTRIES.
			 * Each field is set to the UserBean after it is validated rather than setting all fields   
			 * after validating all fields.
			 */
			public void actionPerformed(ActionEvent arg0) 
			{
				UserBean userBn = new UserBean();
				int proceed = 0;
				
				//set name
				String name = txtName.getText();
				if(!name.isEmpty())
				{
					userBn.setName(name);
				}
				else
				{
					proceed = -1;
					UIUtil.showErrorBox(getRootPane(), "Enter the name!");
				}
				
				//set family name. (it is optional so that no error is shown)
				if(proceed != -1)
				{
					String familyName = txtFamilyName.getText();
					if(!familyName.isEmpty())
					{
						userBn.setName(name);
					}
				}
				
				//set primary mobile number
				if(proceed != -1)
				{
					String primaryMobile = txtPrimaryMobile.getText();
					if(!primaryMobile.isEmpty())
					{
						try
						{
							userBn.setPrimaryNumber(new PhoneNumber(primaryMobile));
						}
						catch(InvalidInputException inE)
						{
							UIUtil.showErrorBox(getRootPane(), "Invalid primary mobile number format! It can" +
									" only be 10 digits");
							proceed = -1;
						}
						
					}
					else
					{
						UIUtil.showErrorBox(getRootPane(), "Enter the primary mobile number!");
						proceed = -1;
					}
				}
				
				//set secondary mobile number. It is optional.
				if(proceed != -1)
				{
					String scndryMobile = txtSecondaryMobile.getText();
					if(!scndryMobile.isEmpty())
					{
						try
						{
							userBn.setSecondaryNumber(new PhoneNumber(scndryMobile));
						}
						catch(InvalidInputException inE)
						{
							UIUtil.showErrorBox(getRootPane(), "Invalid secondary mobile number format! It can" +
									" only be 10 digits");
							proceed = -1;
						}
						
						if(proceed != -1)
						{
							int option = UIUtil.showConfirmationBox(rootPane, "Please confirm the primary  " +
									 "mobile number!", "Only Primary mobile number will be used for all " +
									   "communications. Do you want to edit the primary mobile number?");
											
									if(option == 0)
									{
										proceed = -1;
										UIUtil.showWarningBox(rootPane, "Warning", "Please enter the new "+
												"primary mobile number!");
										
									}
						}
						
					}
				}
				
				//set primary Email Address
				if(proceed != -1)
				{
					String primaryEmail = txtPrimaryEmail.getText();
					if(!primaryEmail.isEmpty())
					{
						try
						{
							userBn.setPrimaryEmail(new EmailAddress(primaryEmail));
						}
						catch(InvalidInputException inE)
						{
							UIUtil.showErrorBox(getRootPane(), "Invalid primary Email Address format!");
							proceed = -1;
						}
						
					}
					else
					{
						UIUtil.showErrorBox(getRootPane(), "Enter the primary Email address!");
						proceed = -1;
					}
				
				}
				
				//set secondary Email Address. It is optional
				if(proceed != -1)
				{
					String secondaryEmail = txtSecondaryEmailAddress.getText();
					if(!secondaryEmail.isEmpty())
					{
						try
						{
							userBn.setSecondaryEmail(new EmailAddress(secondaryEmail));
						}
						catch(InvalidInputException inE)
						{
							UIUtil.showErrorBox(getRootPane(), "Invalid secondary Email address format!");
							proceed = -1;
						}
						
						if(proceed != -1)
						{
							int option = UIUtil.showConfirmationBox(rootPane, "Please confirm the primary  " +
									 "Email Address!", "Only Primary Email Address will be used for all " +
									   "communications. Do you want to edit the primary Email address?");
											
									if(option == 0)
									{
										proceed = -1;
										UIUtil.showWarningBox(rootPane, "Warning", "Please enter the new "+
												"primary Email address!");
										
									}
						}
						
					}
				}
				
				//set home address
				if(proceed != -1)
				{
					String country = null;
					String province = null;
					try
					{
						country = ddlCountry.getSelectedItem().toString();
						if(country.equals("--Select--"))
						{
							UIUtil.showErrorBox(getRootPane(), "Select the country!");
							proceed = -1;
						}
						
					}
					catch(Exception ex)
					{
						UIUtil.showErrorBox(getRootPane(), "Select the country!");
						proceed = -1;
					}
					
					try
					{
						if(proceed != -1)
							province = ddlProvince.getSelectedItem().toString();
					}
					catch(Exception ex)
					{
						UIUtil.showErrorBox(getRootPane(), "Select the province!");
					}

					String city = txtCity.getText();
					String postalCode = txtPostalCode.getText();
					String homeAddress = txtHomeAddress.getText();
					
					if(proceed != -1)
					{
						if(province.equals("----Select Province----------"))
						{
							UIUtil.showErrorBox(getRootPane(), "Select the province!");
							proceed = -1;
						}
					}
					
					if(proceed != -1)
					{
						if(city.isEmpty())
						{
							UIUtil.showErrorBox(getRootPane(), "Enter the city!");
							proceed = -1;
						}
					}
					
					
					if(proceed != -1)
					{
						if(postalCode.isEmpty())
						{
							UIUtil.showErrorBox(getRootPane(), "Enter the postal code!");
							proceed = -1;
						}
					}
					
					if(proceed != -1)
					{
						if(homeAddress.isEmpty())
						{
							UIUtil.showErrorBox(getRootPane(), "Enter the residential address!");
							proceed = -1;
						}
					}
					
					if(proceed != -1)
					{
						try
						{
							userBn.setAddress(new Address(country,province,city,postalCode,homeAddress));
						}
						catch(InvalidInputException e)
						{
							proceed = -1;
							UIUtil.showErrorBox(rootPane, "Please check the address!");
						}
					}
				}
				
				//pass the bean to the control class
				if(proceed != -1)
				{
					int option = UIUtil.showConfirmationBox(rootPane, "Please confirm all the details!", "Are" +
							" you sure that all the details entered are valid?");
					if(option != 0)
					{
						proceed = -1;
						UIUtil.showWarningBox(rootPane, "Warning", "Please correct the details!");
					}
					else
					{ 
						 if(proceed != -1)
						 {
							 frame.dispose();
							 Home.disableButtons(true);
							 DigSigProgressBar.showProgressGUI("Registering User", true);
							 
							 UserRegistration userRegObj = new UserRegistration(Registration.this);
							 userRegObj.registerUser(userBn);
						 }
						
					}
				}
			}
			
		});
		btnRegister.setBounds(160, 238, 89, 23);
		contentPane.add(btnRegister);
		
		
		JLabel lblFamilyName = new JLabel("Family Name");
		lblFamilyName.setBounds(241, 21, 176, 14);
		contentPane.add(lblFamilyName);
		
		txtFamilyName = new JTextField();
		txtFamilyName.setBounds(241, 43, 176, 20);
		contentPane.add(txtFamilyName);
		txtFamilyName.setColumns(10);
		
		JLabel lblSecondaryMobileNumber = new JLabel("Secondary Mobile Number ");
		lblSecondaryMobileNumber.setBounds(241, 63, 176, 14);
		contentPane.add(lblSecondaryMobileNumber);
		
		txtSecondaryMobile = new JTextField();
		txtSecondaryMobile.setBounds(282, 83, 135, 20);
		contentPane.add(txtSecondaryMobile);
		txtSecondaryMobile.setColumns(10);
		
		JLabel lblSecondaryEmailAddress = new JLabel("Secondary Email Address ");
		lblSecondaryEmailAddress.setBounds(241, 105, 176, 14);
		contentPane.add(lblSecondaryEmailAddress);
		
		txtSecondaryEmailAddress = new JTextField();
		txtSecondaryEmailAddress.setBounds(241, 127, 176, 20);
		contentPane.add(txtSecondaryEmailAddress);
		txtSecondaryEmailAddress.setColumns(10);
		
		JLabel lblCity = new JLabel("City ");
		lblCity.setBounds(27, 196, 29, 14);
		contentPane.add(lblCity);
		
		txtHomeAddress = new JTextField();
		txtHomeAddress.setBounds(27, 170, 170, 20);
		contentPane.add(txtHomeAddress);
		txtHomeAddress.setColumns(10);
		
		JLabel lblPostalCode = new JLabel("Postal Code");
		lblPostalCode.setBounds(337, 196, 72, 14);
		contentPane.add(lblPostalCode);
		
		txtPostalCode = new JTextField();
		txtPostalCode.setBounds(337, 211, 80, 20);
		contentPane.add(txtPostalCode);
		txtPostalCode.setColumns(10);
		
		JLabel lblProvince = new JLabel("Province ");
		lblProvince.setBounds(241, 155, 56, 14);
		contentPane.add(lblProvince);
		
		ddlProvince = new JComboBox<Object>();
		ddlProvince.setPrototypeDisplayValue("XXXXXXXXXX"); //JDK1.4
		ddlProvince.setBounds(241, 170, 176, 20);
		contentPane.add(ddlProvince);
		
		String[] statesBrazil = { "----Select Province----------", "Acre", "Alagoas", "Amapa" };
        provinces.put(items[1], statesBrazil);

        String[] prvncesCanada = { "----Select Province----------", "Alberta", "British Columbia", 
        		"Manitoba", "New Brunswick", "Nova Scotia", "Ontario", "Prince Edward Island", 
        		"Quebec", "Saskatchewan" };
        provinces.put(items[2], prvncesCanada);

        String[] statesIndia = {"----Select Province----------", "Andhra Pradesh","Andaman & Nicobar Islands",
        		"Arunachal Pradesh","Assam","Bihar","Chandigarh","Chattisgarh",
        		"Dadra and Nagar Haveli","Daman and Diu","Goa","Gujarat","Haryana",
        		"Himachal Pradesh","Lakshadweep","Jammu and Kashmir","Jharkhand",
        		"Karnataka","Kerala","Madhya Pradesh","Maharashtra","Manipur","Mehalaya","Mizoram",
        		"Nagaland","New Delhi","Odhisha","Punjab","Puducherry","Rajasthan","Sikkim",
        		"Tamil Nadu","Tripura","Telangana","Uttar Pradesh","Uttarakand","West Bengal"};
        provinces.put(items[3], statesIndia);
        
        String[] prvncsUSA = { "----Select Province----------", "Alabama","Alska","Arizona",
        		"Arkansas","California","Colorada","Connecticut","Delaware","Florida","Georgia","Hawaii",
        		"Idaho","Illinois","Indiana","Iowa","Kansas","Kentucky","Louisiana","Maine",
        		"Maryland","Massachusetts","Michigan","Minnesotta","Mississippi","Missouri",
        		"Montana","Nebraska","Nevada","New Hampshire","New Jersey","New Mexico","New York",
        		"North Carolina", "North Dakota","Ohio","Oklahoma","Oregon","Pennsylvania",
        		"Rhode Island","South Carolina","South Dakota","Tennessee","Texas","Utah",
        		"Vermont","Virginia","Washington","West Virginia","Wisconsin","Wyoming"};
        provinces.put(items[4], prvncsUSA);
        
		JLabel lblCountry = new JLabel("Country ");
		lblCountry.setBounds(241, 196, 49, 14);
		contentPane.add(lblCountry);
		
		ddlCountry = new JComboBox<Object>(items);
		ddlCountry.setBounds(241, 211, 86, 20);
		ddlCountry.setModel(new DefaultComboBoxModel<Object>(new String[] {"--Select--", "Brazil", "Canada", 
				"India", "USA"}));
		ddlCountry.setSelectedIndex(0);
		ddlCountry.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				String country = (String)ddlCountry.getSelectedItem();
		        Object obj = provinces.get(country);

		        if (obj == null)
		        {
		            ddlProvince.setModel( new DefaultComboBoxModel<Object>() );
		        }
		        else
		        {
		            ddlProvince.setModel( new DefaultComboBoxModel<Object>( (String[])obj ) );
		            
		            if(country.equals("USA") || country.equals("Canada"))
					{
						txtPrmryMobNoCode.setText("+1");
						
						if(!txtSecondaryMobile.getText().isEmpty())
						{
							txtSecMobNoCode.setText("+1");
						}
						else
						{
							txtSecMobNoCode.setText("");
						}
						
					}
					
					else if(country.equals("India"))
					{
						txtPrmryMobNoCode.setText("+91");
						
						if(!txtSecondaryMobile.getText().isEmpty())
						{
							txtSecMobNoCode.setText("+91");
						}
						else
						{
							txtSecMobNoCode.setText("");
						}
					} 
					
					else if(country.equals("Brazil"))
					{
						txtPrmryMobNoCode.setText("+55");
						
						if(!txtSecondaryMobile.getText().isEmpty())
						{
							txtSecMobNoCode.setText("+55");
						}
						else
						{
							txtSecMobNoCode.setText("");
						}
					} 
		        }
				
			}
		});
		contentPane.add(ddlCountry);
		
		txtPrmryMobNoCode = new JTextField();
		txtPrmryMobNoCode.setEnabled(false);
		txtPrmryMobNoCode.setEditable(false);
		txtPrmryMobNoCode.setBounds(27, 83, 38, 20);
		contentPane.add(txtPrmryMobNoCode);
		txtPrmryMobNoCode.setColumns(10);
		
		txtSecMobNoCode = new JTextField();
		txtSecMobNoCode.setEnabled(false);
		txtSecMobNoCode.setEditable(false);
		txtSecMobNoCode.setBounds(241, 83, 38, 20);
		contentPane.add(txtSecMobNoCode);
		txtSecMobNoCode.setColumns(10);
		
		JLabel lblNameReqd = new JLabel("*");
		lblNameReqd.setForeground(Color.RED);
		lblNameReqd.setBounds(62, 21, 14, 14);
		contentPane.add(lblNameReqd);
		
		JLabel lblPrmMobReqd = new JLabel(" *");
		lblPrmMobReqd.setForeground(Color.RED);
		lblPrmMobReqd.setBounds(160, 63, 20, 14);
		contentPane.add(lblPrmMobReqd);
		
		JLabel lblCityMan = new JLabel("*");
		lblCityMan.setForeground(Color.RED);
		lblCityMan.setBounds(51, 196, 14, 14);
		contentPane.add(lblCityMan);
		
		JLabel lblEmailReq = new JLabel("*");
		lblEmailReq.setForeground(Color.RED);
		lblEmailReq.setBounds(160, 105, 14, 14);
		contentPane.add(lblEmailReq);
		
		JLabel lblAddressReqd = new JLabel("*");
		lblAddressReqd.setForeground(Color.RED);
		lblAddressReqd.setBounds(147, 158, 14, 14);
		contentPane.add(lblAddressReqd);
		
		JLabel lblPrvnReqd = new JLabel("*");
		lblPrvnReqd.setForeground(Color.RED);
		lblPrvnReqd.setBounds(296, 155, 20, 14);
		contentPane.add(lblPrvnReqd);
		
		JLabel lblReqdPos = new JLabel(" *");
		lblReqdPos.setForeground(Color.RED);
		lblReqdPos.setBounds(403, 196, 14, 14);
		contentPane.add(lblReqdPos);
		
		JLabel lblCountryReqd = new JLabel("*");
		lblCountryReqd.setForeground(Color.RED);
		lblCountryReqd.setBounds(289, 196, 14, 14);
		contentPane.add(lblCountryReqd);
		
		JButton btnQuit = new JButton("Close");
		btnQuit.setForeground(Color.RED);
		btnQuit.setBounds(354, 11, 72, 23);
		btnQuit.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				int option = UIUtil.showConfirmationBox(rootPane, "Confirm your action!"
						, "Are you sure that you want to leave the registration?");
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
		DigSigProgressBar.showProgressGUI("", false);
		Home.disableButtons(false);
		switch (type) {
		case SocketMessageType.ACK_REGISTRATION_SUCCESSFUL:
			UIUtil.showInformationBox(rootPane, "Registration successful!" ," Thanks for registering! " +
				"Please check your registered Email Address and mobile!");
			break;
			
		case SocketMessageType.ACK_REGISTRATION_FAILURE:
			int status = (Integer) content;
			
			switch(status) 
			{
			
			case -2:
				UIUtil.showInformationBox(rootPane, "Registration failure!" ," User already exists! Primary Email" +
						 " address you had entered is in use! Please contact the service team!");
						break;
			default:
				UIUtil.showInformationBox(rootPane, "Registration failure!" ," Sorry! An error occurred! Please " +
				 " contact the service team! Error type:  Error info: " + ((Integer) content).toString());
				break;
		
		    }
		}
		
	}
}
