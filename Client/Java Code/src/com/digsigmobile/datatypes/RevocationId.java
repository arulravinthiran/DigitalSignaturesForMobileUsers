package com.digsigmobile.datatypes;

import java.io.Serializable;

import com.digsigmobile.exceptions.InvalidInputException;

public class RevocationId implements Serializable
{
	/**
	 * serialVersionUID of Serializable interface
	 */
	
	private static final long serialVersionUID = 1L;
	private int id;
	
	public RevocationId(int id) throws InvalidInputException {
		setId(id);
	}

	public int getId() {
		return id;
	}

	private void setId(int id) throws InvalidInputException {
		this.id = id;
		if (!isValid()) throw new InvalidInputException("Not a valid Revocation Id");
	}

	private boolean isValid() {
		if (this.id >= 0) {
			return true;
		}
		return false;
	}
}
