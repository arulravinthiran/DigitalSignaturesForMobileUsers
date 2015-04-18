package com.digsigmobile.exceptions;

/**
 * Custom data validation exception
 * @author -Bernardo
 *
 */

public class InvalidInputException extends Exception {

	private static final long serialVersionUID = 5423993923396405414L;

	public InvalidInputException(String message) {
		super(message);
	}
}

