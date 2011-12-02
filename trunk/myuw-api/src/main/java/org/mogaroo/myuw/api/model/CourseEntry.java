package org.mogaroo.myuw.api.model;

import java.util.List;

public class CourseEntry {
	private final CourseIdentifier _courseId;
	private final CourseDescription _description;
	private final List<PreRequisite> _preReqs;
	private final SectionLabel _sectionLabel;
	private final ScheduleLineNumber _sln;
	
	public CourseEntry(CourseIdentifier courseId, CourseDescription description, List<PreRequisite> preReqs,
			SectionLabel label, ScheduleLineNumber sln) {
		_courseId = courseId;
		_description = description;
		_preReqs = preReqs;
		_sectionLabel = label;
		_sln = sln;
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
	
	public SectionLabel getSectionLabel() {
		return _sectionLabel;
	}
	
	public ScheduleLineNumber getSln() {
		return _sln;
	}
	
	@Override
	public String toString() {
		return _courseId.getDepartment().getAbbreviation() + "," + _courseId.getCourseLevel().getLevel() 
			+ "," + _sectionLabel.getLabel() + "," + _sln.getValue();
	}
}
