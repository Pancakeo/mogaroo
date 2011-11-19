package org.mogaroo.myuw.api.model;

public class CourseSection {
	
	private Course _course;
	private SectionLabel _sectionLabel;
	private SectionType _sectionType;
	private Instructor _instructor;
	private CourseTimes _times;
	private ScheduleLineNumber _sln;
	private int _currentEnrollment;
	private int _enrollmentLimit;
	
	protected CourseSection(Course course, ScheduleLineNumber sln, SectionLabel label,
			SectionType type, Instructor instructor, CourseTimes times,
			int currentEnrollment, int enrollmentLimit) {
		_course = course;
		_sln = sln;
		_sectionLabel = label;
		_sectionType = type;
		_instructor = instructor;
		_times = times;
		_currentEnrollment = currentEnrollment;
		_enrollmentLimit = enrollmentLimit;
	}
	
	public Course getCourse() {
		return _course;
	}

	public SectionLabel getSectionLabel() {
		return _sectionLabel;
	}

	public SectionType getSectionType() {
		return _sectionType;
	}
	
	public Instructor getInstructor() {
		return _instructor;
	}

	public CourseTimes getTimes() {
		return _times;
	}

	public ScheduleLineNumber getSln() {
		return _sln;
	}

	public int getCurrentEnrollment() {
		return _currentEnrollment;
	}

	public int getEnrollmentLimit() {
		return _enrollmentLimit;
	}

	public enum SectionType {
		LECTURE,
		QUIZ,
		LAB
	}
}
