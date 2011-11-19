package org.mogaroo.myuw.api.model;

public class PreRequisite {
	
	private CourseIdentifier _courseId;
	private Grade _minimumGrade;
	
	protected PreRequisite(CourseIdentifier courseId, Grade minGrade) {
		_courseId = courseId;
		_minimumGrade = minGrade;
	}

	public CourseIdentifier getCourseId() {
		return _courseId;
	}

	public Grade getMinimumGrade() {
		return _minimumGrade;
	}
}
