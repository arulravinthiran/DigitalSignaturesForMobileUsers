package com.digsigmobile.socket.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.digsigmobile.persistence.Persistence;

public class Server {
	// a unique ID for each connection
	public static int uniqueId;
	// an ArrayList to keep the list of the Client
	private ArrayList<ClientThread> list;
	// the port number to listen for connection
	private int port;
	// the boolean that will be turned off to stop the server
	private boolean keepGoing;

	public Server(int port) {
		this.port = port;
		list = new ArrayList<ClientThread>();
	}

	public void start() {
		keepGoing = true;
		/* create socket server and wait for connection requests */
		try {
			// the socket used by the server
			ServerSocket serverSocket = new ServerSocket(port);
			
			// infinite loop to wait for connections
			while(keepGoing) {
				// format message saying we are waiting
				display("Server waiting for Clients on port " + port + ".");

				Socket socket = serverSocket.accept();  	// accept connection
				// if I was asked to stop
				if(!keepGoing)
					break;
				ClientThread t = new ClientThread(this,socket);  // make a thread of it
				list.add(t);									// save it in the ArrayList
				t.start();
			}
			// I was asked to stop
			try {
				serverSocket.close();
				Persistence persistence = Persistence.getInstance();
				if (persistence.isConnected())
					persistence.disconnect();
				for(int i = 0; i < list.size(); ++i) {
					ClientThread tc = list.get(i);
					try {
						tc.sInput.close();
						tc.sOutput.close();
						tc.socket.close();
					}
					catch(IOException ioE) {
						// not much I can do
					}
				}
			}
			catch(Exception e) {
				display("Exception closing the server and clients: " + e);
			}
		}
		// something went bad
		catch (IOException e) {
			display(" Exception on new ServerSocket: " + e + "\n");
		}
	}

	protected void stop() {
		keepGoing = false;
		// connect to myself as Client to exit statement 
		// Socket socket = serverSocket.accept();
		try {
			new Socket("localhost", port);
		}
		catch(Exception e) {
			// nothing I can really do
		}
	}
	
	private void display(String msg) {
		System.out.println(msg);
	}

	// for a client who logoff using the LOGOUT message
	public synchronized void remove(int id) {
		// scan the array list until we found the Id
		for(int i = 0; i < list.size(); ++i) {
			ClientThread ct = list.get(i);
			// found it
			if(ct.id == id) {
				list.remove(i);
				return;
			}
		}
	}
	
	public static void main(String[] args) {
		// start server on port 6000 unless a PortNumber is specified 
		int portNumber = 6000;
		switch(args.length) {
		case 1:
			try {
				portNumber = Integer.parseInt(args[0]);
			}
			catch(Exception e) {
				System.out.println("Invalid port number.");
				System.out.println("Usage is: > java Server [portNumber]");
				return;
			}
		case 0:
			break;
		default:
			System.out.println("Usage is: > java Server [portNumber]");
			return;

		}
		// create a server object and start it
		Server server = new Server(portNumber);
		server.start();
	}
}
