package org.mogaroo.myuw.api.model;

public class Instructor {
	
	private String _name;
	
	protected Instructor(String name) {
		_name = name;
	}
	
	public String getName() {
		return _name;
	}
}
