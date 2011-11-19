package org.mogaroo.myuw.api.model;

public class CourseIdentifier {
	
	private Department _department;
	private CourseLevel _courseLevel;
	
	public CourseIdentifier(Department department, CourseLevel level) {
		if (department == null) {
			throw new IllegalArgumentException("Department may not be null.");
		}
		if (level == null) {
			throw new IllegalArgumentException("CourseLevel may not be null.");
		}
		_department = department;
		_courseLevel = level;
	}

	public Department getDepartment() {
		return _department;
	}

	public CourseLevel getCourseLevel() {
		return _courseLevel;
	}
}
