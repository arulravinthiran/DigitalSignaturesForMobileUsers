package com.digsigmobile.datatypes;

import com.digsigmobile.exceptions.InvalidInputException;

public class RevocationId {
	
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

