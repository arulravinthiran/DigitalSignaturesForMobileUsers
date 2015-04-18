package com.digsigmobile.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class SMSProvider {

	private static final String targetURL = "http://192.168.1.101:9090/";
	private static final String password = "digsig";
	private String response;
	
	public String getResponse() {
		return response;
	}

	/**
	 * This method will make a HTTP GET request to the target URL
	 * which is a host that must be running in an android phone with a
	 * SMS gateway activated.
	 * The Android app that manages the host is called "SMS Gateway"
	 * - yeah, pretty original. As original as digsig mobile =D -
	 * This app expects 3 parameters from the GET request:
	 *     -> phone: A phone number to send the message
	 *     -> text: The message to send
	 *     -> password: Just a - not much secure - password. Configured to "digsig"
	 */
	public void sendSMS(String phoneNumber, String message) {
		String urlWithParameters = targetURL;
		HttpConnection connection = new HttpConnection(); 
		
		// Add parameters to URL
		try {
			urlWithParameters += 
					"sendsms?phone=" + URLEncoder.encode(phoneNumber, "UTF-8") +
			        "&text=" + URLEncoder.encode(message, "UTF-8") +
			        "&password=" + URLEncoder.encode(password, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			response = "Unable to send SMS message. Verify message characters and try again";
		}
		
		// Connect to SMS Gateway
		connection.sendGet(urlWithParameters);
		
		// Verify if it worked
		if (connection.getResponseCode() == 200) {
			response = "SMS message sent successfully";
		} else {
			response = "Unable to send SMS message";
		}
	}

}