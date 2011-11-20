package org.mogaroo.myuw.api;

public class MyUWServiceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5917156070658188693L;
	
	public MyUWServiceException(String message) {
		super(message);
	}
	
	public MyUWServiceException(String message, Throwable t) {
		super(message, t);
	}

}
