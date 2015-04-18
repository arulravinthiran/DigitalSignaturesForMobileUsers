package com.digsigmobile.socket.message;

import java.io.Serializable;

public class SocketMessage implements Serializable{

	private static final long serialVersionUID = 6348522686290966281L;
	
	private int type;
	private Serializable content;
	
	public SocketMessage(int type, Serializable content) {
		this.type = type;
		this.content = content;
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public Serializable getContent() {
		return content;
	}
	
	public void setContent(Serializable content) {
		this.content = content;
	}
	
}
