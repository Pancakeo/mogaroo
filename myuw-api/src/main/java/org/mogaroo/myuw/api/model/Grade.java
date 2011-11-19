package org.mogaroo.myuw.api.model;

public class Grade {
	
	private float _gpa;
	
	protected Grade(int gpa) {
		_gpa = gpa;
	}
	
	public float getGPA() {
		return _gpa;
	}
}
