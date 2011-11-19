package org.mogaroo.myuw.api.model;

import java.util.List;

public class CourseTimes {
	
	private List<CourseTime> _times;
	
	protected CourseTimes(List<CourseTime> times) {
		_times = times;
	}
	
	public List<CourseTime> getTimes() {
		return _times;
	}
}
