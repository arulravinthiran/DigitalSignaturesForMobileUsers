package com.digsigmobile.socket.client;

import java.io.IOException;
import java.io.ObjectInputStream;

import com.digsigmobile.socket.client.interfaces.ResponseListener;
import com.digsigmobile.socket.message.SocketMessage;

public class ListenFromServer extends Thread {

	private ObjectInputStream sInput;		// to read from the socket
	private ResponseListener delegate;
	
	public ListenFromServer(ObjectInputStream input, ResponseListener delegate) {
		this.sInput = input;
		this.delegate = delegate;
	}
	
	public void setResponseListener(ResponseListener listener) {
		this.delegate = listener;
	}

	public void run() {
		while(true) {
			try {
				final SocketMessage msg = (SocketMessage) sInput.readObject();
				delegate.onResponse(msg.getType(), msg.getContent());
			} catch(IOException e) {
				System.out.println("Server closed the connection: " + e);
				delegate.onServerClosed(e);
				break;
			} catch(ClassNotFoundException e2) {
				e2.printStackTrace();
			}
		}
	}

}
