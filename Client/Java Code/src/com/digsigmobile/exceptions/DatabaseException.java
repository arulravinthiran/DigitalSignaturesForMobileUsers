package com.digsigmobile.exceptions;

/**
 * Custom Database Exception
 * @author -Bernardo
 *
 */

public class DatabaseException extends Exception {

	private static final long serialVersionUID = -7520512190490309765L;

	public DatabaseException(String message) {
		super(message);
	}

}

