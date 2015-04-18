package com.digsigmobile.util;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailProvider {
	private static final String username = "digsigmobile@gmail.com";
	private static final String password = "digsigPR0J3C7";
	private static final String host = "smtp.gmail.com";
	private static final String port = "587";
	private String response;
	private boolean sent = false;
	
	public String getResponse() {
		return response;
	}

	public void sendMail(String toEmail, String subject, String msg) {
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", port);
 
		Session session = Session.getInstance(props,
		  new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		  });
 
		try {
 
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username));
			message.setRecipients(Message.RecipientType.TO,
				InternetAddress.parse(toEmail));
			message.setSubject(subject);
			message.setText(msg);
 
			Transport.send(message);
 
			response = "Email sent";
			setSent(true);
 
		} catch (Exception e) {
			response = "Email could not be sent";
			setSent(false);
		}
	}

	public boolean isSent() {
		return sent;
	}

	public void setSent(boolean sent) {
		this.sent = sent;
	}
}
