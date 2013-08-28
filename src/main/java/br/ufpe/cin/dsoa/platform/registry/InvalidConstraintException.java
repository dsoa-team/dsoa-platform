package br.ufpe.cin.dsoa.platform.registry;

import org.osgi.framework.InvalidSyntaxException;

public class InvalidConstraintException extends RuntimeException {

	private String message;
	
	public InvalidConstraintException(String message, InvalidSyntaxException e) {
		super(e);
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
