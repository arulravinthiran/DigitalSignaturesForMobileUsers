package com.digsigmobile.control;

import java.io.Serializable;

import com.digsigmobile.datatypes.TrustCode;
import com.digsigmobile.socket.client.SocketManager;
import com.digsigmobile.socket.client.interfaces.ResponseListener;
import com.digsigmobile.socket.client.interfaces.UICallback;
import com.digsigmobile.socket.message.SocketMessage;
import com.digsigmobile.socket.message.SocketMessageType;

public class TrustCodeVerification implements ResponseListener {
	/**
	 * Socket Manager
	 */
	private SocketManager socketManager = null;
	
	/**
	 * Delegate UI interface
	 */
	private UICallback ui;
	
	public TrustCodeVerification(UICallback ui) {
		this.socketManager = SocketManager.getInstance("localhost", 6000, this, null);
		this.ui = ui;
	}
	
	public void verifyTrustCode(TrustCode trCode) {
		
		// verify trust code
		SocketMessage action = new SocketMessage(SocketMessageType.ACTION_TRUSTCODE_VERIFICATION, trCode);
		socketManager.sendMessage(action);
		
		/*DigSigMobClientSocketHelper digSigClntHlper = new DigSigMobClientSocketHelper();
		digSigClntHlper.sendTrustCode(trCode);
		Map<Integer,Row> rowMap = new HashMap<Integer, Row>();
		beanObjects = digSigClntHlper.receiveSet(ACTION_TRUSTCODE_VERIFICATION);
		return beanObjects;*/
		
		//Below lines are hard-coded for testing. Shd be removed
		/*
		Map<Integer,Row> rowMap = new HashMap<Integer, Row>();
		Row rowObj = null;
		
		UserBean userBnObj = new UserBean();
		
		userBnObj.setFamilyName("Ravinthiran");
		userBnObj.setName("Arul");
		
		DigitalSignatureBean digSigBnObj = new DigitalSignatureBean();
		
		digSigBnObj.setHasSigned(false);
		
		rowObj = new Row(userBnObj, digSigBnObj);
		rowMap.put(0, rowObj);
		
		userBnObj = new UserBean();
		digSigBnObj = new DigitalSignatureBean();
		
		userBnObj.setFamilyName("Macedo");
		userBnObj.setName("Bernado");
		
		digSigBnObj.setHasSigned(true);
		digSigBnObj.setIsValid(true);
		digSigBnObj.setSignedTime(new Timestamp(new Date().getTime()));
		digSigBnObj.setSigningReason("Author");
		
		rowObj = new Row(userBnObj, digSigBnObj);
		rowMap.put(1, rowObj);
		
		return rowMap;
		*/
	}

	@Override
	public void onResponse(int type, Serializable content) {
		// In case of success or in case of failure, just send the result
		// to the UI
		ui.onCallback(type, content);
	}

	@Override
	public void onServerClosed(Exception e) {
		// TODO Auto-generated method stub
		
	}

}
