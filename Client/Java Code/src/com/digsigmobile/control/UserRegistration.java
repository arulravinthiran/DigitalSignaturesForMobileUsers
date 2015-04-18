package com.digsigmobile.control;

import java.io.IOException;
import java.io.Serializable;

import com.digsigmobile.beans.UserBean;
import com.digsigmobile.socket.client.SocketManager;
import com.digsigmobile.socket.client.interfaces.ResponseListener;
import com.digsigmobile.socket.client.interfaces.UICallback;
import com.digsigmobile.socket.message.SocketMessage;
import com.digsigmobile.socket.message.SocketMessageType;

public class UserRegistration implements ResponseListener {
	
	/**
	 * SocketManager
	 */
	private SocketManager socketManager = null;
	
	/**
	 * Delegate UI interface
	 */
	private UICallback ui = null;
	
	public UserRegistration(UICallback ui) {
		socketManager = SocketManager.getInstance("localhost", 6000, this, null);
		this.ui = ui;
	}
	
	/**
	 * interacts with the server and 
	 * retrieves the user id on successful registration of the user
	 * @param user
	 * @return
	 * @throws IOException 
	 */
	public void registerUser(UserBean user) {
		// register user
		SocketMessage action = new SocketMessage(SocketMessageType.ACTION_USER_REGISTRATION, user);
		socketManager.sendMessage(action);
		
		/*
		DigSigMobClientSocketHelper sktHlperObj = new DigSigMobClientSocketHelper();
		sktHlperObj.sendUser(user);
		int status = sktHlperObj.receiveInt(ACTION_USER_REGISTRATION);
		return status;
		/* //just test
		return 0;* /
		*/
	}

	@Override
	public void onResponse(int type, Serializable content) {
		switch(type) {
		case SocketMessageType.ACK_REGISTRATION_FAILURE:
			ui.onCallback(type, content);
			break;
		case SocketMessageType.ACK_REGISTRATION_SUCCESSFUL:
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
