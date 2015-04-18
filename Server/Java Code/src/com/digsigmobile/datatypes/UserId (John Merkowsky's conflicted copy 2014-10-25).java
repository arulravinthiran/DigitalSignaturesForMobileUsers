package com.digsigmobile.datatypes;

import java.io.Serializable;

import com.digsigmobile.exceptions.InvalidInputException;

public class UserId implements Serializable
{
	/**
	 * serialVersionUID of Serializable interface
	 */
	private static final long serialVersionUID = 1L;
	
	private int id;
	
	public UserId(int id) throws InvalidInputException {
		setId(id);
	}

	public int getId() {
		return id;
	}

	private void setId(int id) throws InvalidInputException {
		this.id = id;
		if (!isValid()) throw new InvalidInputException("Not a valid User Id");
	}

	//valid UserIds >= 0. 
	private boolean isValid() {
		if (this.id >= 0) {
			return true;
		}
		return false;
	}
}