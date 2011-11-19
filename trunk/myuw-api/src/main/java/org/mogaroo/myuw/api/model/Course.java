package org.mogaroo.myuw.api.model;

import java.util.List;

public class Course {
	
	private CourseIdentifier _courseId;
	private CourseDescription _description;
	private List<PreRequisite> _preReqs;
	
	protected Course(CourseIdentifier courseId, CourseDescription description, List<PreRequisite> preReqs) {
		_courseId = courseId;
		_description = description;
		_preReqs = preReqs;
	}

	public CourseIdentifier getCourseId() {
		return _courseId;
	}

	public CourseDescription getDescription() {
		return _description;
	}

	public List<PreRequisite> getPreReqs() {
		return _preReqs;
	}
	
}
