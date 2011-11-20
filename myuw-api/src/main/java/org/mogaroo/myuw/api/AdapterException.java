package org.mogaroo.myuw.api;

public class AdapterException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6368665653395431990L;
	
	public AdapterException(String message) {
		super(message);
	}
	
	public AdapterException(Throwable t) {
		super(t);
	}
	
	public AdapterException(String message, Throwable t) {
		super(message, t);
	}

}
