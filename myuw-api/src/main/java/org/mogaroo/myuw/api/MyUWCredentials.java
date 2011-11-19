package org.mogaroo.myuw.api;

public class MyUWCredentials {
	
	private String _username;
	private String _password;
	
	public MyUWCredentials(String username, String password) {
		_username = username;
		_password = password;
	}
	
	public String getUsername() {
		return _username;
	}
	
	public String getPassword() {
		return _password;
	}
}
