package com.digsigmobile.socket.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.digsigmobile.socket.client.interfaces.ConnectionListener;
import com.digsigmobile.socket.client.interfaces.ResponseListener;
import com.digsigmobile.socket.message.SocketMessage;
import com.digsigmobile.socket.message.SocketMessageType;

public class SocketManager {

	/**
	 * Singleton pattern.
	 * Unique reference
	 */
	private static SocketManager socketManager = null;
	
	/**
	 * Connection variables
	 */
	private ObjectInputStream sInput = null;		// to read from the socket
	private ObjectOutputStream sOutput = null;		// to write on the socket
	private Socket socket = null;
	private boolean isConnected = false;

	/**
	 * Configuration variables
	 */
	private int port = 1500;
	private String server = "localhost";

	/**
	 * Delegate interfaces
	 */
	private ResponseListener respListener = null;
	private ConnectionListener connListener = null;

	/**
	 * Server listener thread
	 */
	private ListenFromServer listenFromServer = null;

	/*
	 * Make the constructor private
	 */
	private SocketManager() {}
	
	/*
	 * Actual constructor, also private
	 */
	private SocketManager(String server, int port, ResponseListener respListener, ConnectionListener connListener) {
		this.server = server;
		this.port = port;
		this.respListener = respListener;
		this.connListener = connListener;
	}
	
	/*
	 * Return the only possible instance of this class
	 */
	public static SocketManager getInstance(String server, int port, ResponseListener respListener, ConnectionListener connListener) {
		if (socketManager == null) {
			socketManager = new SocketManager(server, port, respListener, connListener);
		} else {
			if (connListener != null) socketManager.setConnectionListener(connListener);
			if (respListener != null) socketManager.setResponseListener(respListener);
		}
		return socketManager;
	}

	public void setResponseListener(ResponseListener listener) {
		this.respListener = listener;
		if (this.listenFromServer != null)
			this.listenFromServer.setResponseListener(listener);
	}

	public void setConnectionListener(ConnectionListener listener) {
		this.connListener = listener;
	}

	/**
	 * Connects to the server
	 */
	public void connect() {
		/*
		 * Open connection
		 */
		new Thread(
			new Runnable() {
				@Override
				public void run() {
					try {
						socket = new Socket(server, port);
						sOutput = new ObjectOutputStream(socket.getOutputStream());
						sInput  = new ObjectInputStream(socket.getInputStream());

						connListener.onSocketConnected();
						isConnected = true;
					} catch(Exception ec) {
						System.out.println("Error connectiong to server:" + ec);
						connListener.onConnectionFailed();
					}
				}
			}
		).start();
	}

	/**
	 * When something goes wrong
	 * Close the Input/Output streams and disconnect not much to do in the catch clause
	 */
	protected void disconnect() {
		try { 
			if(sInput != null) sInput.close();
			if(sOutput != null) sOutput.close();
			if(socket != null) socket.close();
		} catch(Exception e) {}
	}

	/**
	 * Start the client
	 */
	public void start() {
		// creates the Thread to listen from the server 
		listenFromServer = new ListenFromServer(sInput, respListener);
		listenFromServer.start();
		new Thread(
			new Runnable() {
				@Override
				public void run() {
					try {
						SocketMessage ack = new SocketMessage(SocketMessageType.ACTION_CONNECT, "");
						sOutput.writeObject(ack);
					} catch (IOException eIO) {
						System.out.println("Exception doing login : " + eIO);
						disconnect();
					}
				}
			}
		).start();
	}

	/**
	 * To send a message to the server
	 */
	public void sendMessage(final SocketMessage msg) {
		new Thread(
			new Runnable() {
				@Override
				public void run() {
					if (socket != null && sOutput != null) {
						try {
							sOutput.writeObject(msg);
						} catch (IOException e) {
							e.printStackTrace();
						}
					} else {
						System.out.println("socket == " + socket);
						System.out.println("sOutput == " + sOutput);
					}
				}
			}
		).start();
	}

	public boolean isConnected() {
		return isConnected;
	}
}
