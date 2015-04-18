package com.digsigmobile.socket.client.interfaces;

import java.io.Serializable;

public interface ResponseListener {
	public void onResponse(int type, Serializable content);
	public void onServerClosed(Exception e);
}
