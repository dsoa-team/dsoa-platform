package br.ufpe.cin.dsoa.platform.registry;

import org.osgi.framework.InvalidSyntaxException;

public class InvalidConstraintException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6448160017317723239L;
	private String message;
	
	public InvalidConstraintException(String message, InvalidSyntaxException e) {
		super(e);
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
