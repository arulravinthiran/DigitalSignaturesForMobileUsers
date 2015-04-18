package com.digsigmobile.socket.client.interfaces;

import java.io.Serializable;

public interface UICallback {

	public void onCallback(int type, Serializable content);
	
}
