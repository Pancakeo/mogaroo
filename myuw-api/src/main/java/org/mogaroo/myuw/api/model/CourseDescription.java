package org.mogaroo.myuw.api.model;

public class CourseDescription {
	
	private String _description;
	
	protected CourseDescription(String description) {
		_description = description;
	}
	
	public String getDescription() {
		return _description;
	}
}
